#!/usr/bin/env python3
"""
Memorin Redis缓存管理工具
提供缓存预热、清理、监控、一致性检查等功能

使用方法:
    python redis_cache_management.py --warmup  # 缓存预热
    python redis_cache_management.py --cleanup # 缓存清理
    python redis_cache_management.py --monitor # 监控指标
    python redis_cache_management.py --check   # 一致性检查
"""

import redis
import mysql.connector
import json
import gzip
import base64
import time
import random
import argparse
import logging
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional
from redis.connection import ConnectionPool
import sys
import os

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('/var/log/redis/cache_management.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

class RedisCacheManager:
    """Redis缓存管理器"""
    
    def __init__(self, config: Dict[str, Any]):
        """初始化缓存管理器"""
        self.config = config
        self.redis_master = None
        self.redis_slave = None
        self.db_connection = None
        self.hit_count = 0
        self.miss_count = 0
        self.total_requests = 0
        
        self._init_redis_connections()
        self._init_db_connection()
    
    def _init_redis_connections(self):
        """初始化Redis连接"""
        try:
            # 主Redis连接池
            master_pool = ConnectionPool(
                host=self.config['redis']['master']['host'],
                port=self.config['redis']['master']['port'],
                password=self.config['redis']['master'].get('password'),
                db=0,
                max_connections=100,
                retry_on_timeout=True,
                socket_timeout=5,
                socket_connect_timeout=5,
                health_check_interval=30
            )
            self.redis_master = redis.Redis(connection_pool=master_pool)
            
            # 从Redis连接池
            if 'slave' in self.config['redis']:
                slave_pool = ConnectionPool(
                    host=self.config['redis']['slave']['host'],
                    port=self.config['redis']['slave']['port'],
                    password=self.config['redis']['slave'].get('password'),
                    db=0,
                    max_connections=50,
                    retry_on_timeout=True,
                    socket_timeout=5,
                    socket_connect_timeout=5,
                    health_check_interval=30
                )
                self.redis_slave = redis.Redis(connection_pool=slave_pool)
            else:
                self.redis_slave = self.redis_master
            
            logger.info("Redis连接初始化成功")
            
        except Exception as e:
            logger.error(f"Redis连接初始化失败: {e}")
            sys.exit(1)
    
    def _init_db_connection(self):
        """初始化数据库连接"""
        try:
            self.db_connection = mysql.connector.connect(
                host=self.config['database']['host'],
                port=self.config['database']['port'],
                user=self.config['database']['user'],
                password=self.config['database']['password'],
                database=self.config['database']['name'],
                autocommit=True,
                charset='utf8mb4'
            )
            logger.info("数据库连接初始化成功")
            
        except Exception as e:
            logger.error(f"数据库连接初始化失败: {e}")
            sys.exit(1)
    
    def warmup_cache(self):
        """系统启动时的缓存预热"""
        logger.info("开始缓存预热...")
        
        try:
            # 1. 预热热点知识库
            self._warmup_popular_knowledge_bases()
            
            # 2. 预热系统配置
            self._warmup_system_configs()
            
            # 3. 预热全局统计
            self._warmup_global_statistics()
            
            # 4. 预热用户会话（最近活跃用户）
            self._warmup_active_user_sessions()
            
            logger.info("缓存预热完成!")
            
        except Exception as e:
            logger.error(f"缓存预热失败: {e}")
            raise
    
    def _warmup_popular_knowledge_bases(self):
        """预热热点知识库"""
        logger.info("预热热点知识库...")
        
        cursor = self.db_connection.cursor(dictionary=True)
        
        # 获取热门知识库
        cursor.execute("""
            SELECT id, name, owner_id, visibility, description, icon, difficulty_level,
                   content_count, subscriber_count, created_at, updated_at
            FROM knowledge_bases 
            WHERE visibility = 'public' 
            ORDER BY subscriber_count DESC 
            LIMIT %s
        """, [self.config['cache']['warmup_popular_kb_count']])
        
        popular_kbs = cursor.fetchall()
        
        for kb in popular_kbs:
            self._preload_knowledge_base(kb['id'], kb)
        
        cursor.close()
        logger.info(f"预热了 {len(popular_kbs)} 个热门知识库")
    
    def _preload_knowledge_base(self, kb_id: int, kb_info: Dict[str, Any]):
        """预加载知识库数据"""
        try:
            # 1. 缓存基本信息
            kb_cache_data = {
                'id': str(kb_info['id']),
                'name': kb_info['name'],
                'owner_id': str(kb_info['owner_id']),
                'visibility': kb_info['visibility'],
                'content_count': str(kb_info['content_count']),
                'subscriber_count': str(kb_info['subscriber_count']),
                'created_at': kb_info['created_at'].isoformat() if kb_info['created_at'] else '',
                'updated_at': kb_info['updated_at'].isoformat() if kb_info['updated_at'] else ''
            }
            
            self.redis_master.hset(f"kb:info:{kb_id}", mapping=kb_cache_data)
            self.redis_master.expire(f"kb:info:{kb_id}", 604800)  # 7天
            
            # 2. 缓存详细信息
            kb_detail = {
                'metadata': {
                    'description': kb_info['description'] or '',
                    'icon': kb_info['icon'] or '📚',
                    'difficulty_level': float(kb_info['difficulty_level'] or 0),
                    'tags': []
                },
                'areas': self._get_knowledge_areas(kb_id),
                'statistics': self._get_kb_statistics(kb_id)
            }
            
            compressed_detail = self._compress_large_object(kb_detail)
            self.redis_master.setex(f"kb:detail:{kb_id}", 604800, compressed_detail)
            
            # 3. 预加载热点知识点
            self._preload_hot_knowledge_points(kb_id)
            
        except Exception as e:
            logger.error(f"预加载知识库 {kb_id} 失败: {e}")
    
    def _get_knowledge_areas(self, kb_id: int) -> List[Dict[str, Any]]:
        """获取知识库的知识区域"""
        cursor = self.db_connection.cursor(dictionary=True)
        cursor.execute("""
            SELECT id, name, description, parent_id, level, sort_order
            FROM knowledge_areas 
            WHERE knowledge_base_id = %s 
            ORDER BY level, sort_order
        """, [kb_id])
        
        areas = cursor.fetchall()
        cursor.close()
        
        return [
            {
                'id': area['id'],
                'name': area['name'],
                'description': area['description'] or '',
                'parent_id': area['parent_id'],
                'level': area['level'],
                'sort_order': area['sort_order']
            }
            for area in areas
        ]
    
    def _get_kb_statistics(self, kb_id: int) -> Dict[str, Any]:
        """获取知识库统计信息"""
        cursor = self.db_connection.cursor(dictionary=True)
        
        # 基础统计
        cursor.execute("""
            SELECT 
                COUNT(*) as total_contents,
                AVG(difficulty_level) as avg_difficulty
            FROM knowledge_point_contents kpc
            JOIN knowledge_base_content_relations kbcr ON kpc.id = kbcr.content_id
            WHERE kbcr.knowledge_base_id = %s
        """, [kb_id])
        
        stats = cursor.fetchone()
        cursor.close()
        
        return {
            'total_contents': stats['total_contents'] or 0,
            'avg_difficulty': float(stats['avg_difficulty'] or 0),
            'completion_rate': 0.0,  # 需要用户登录后计算
            'last_updated': datetime.now().isoformat()
        }
    
    def _preload_hot_knowledge_points(self, kb_id: int):
        """预加载热点知识点"""
        cursor = self.db_connection.cursor(dictionary=True)
        
        # 获取热点内容
        cursor.execute("""
            SELECT kpc.id, kpc.question, kpc.answer, kpc.type, kpc.difficulty_level, 
                   kpc.explanation, kbcr.usage_count
            FROM knowledge_point_contents kpc
            JOIN knowledge_base_content_relations kbcr ON kpc.id = kbcr.content_id
            WHERE kbcr.knowledge_base_id = %s 
            ORDER BY kbcr.usage_count DESC 
            LIMIT %s
        """, [kb_id, self.config['cache']['warmup_hot_content_count']])
        
        hot_contents = cursor.fetchall()
        
        for content in hot_contents:
            self._preload_knowledge_point(content)
        
        cursor.close()
        logger.info(f"为知识库 {kb_id} 预加载了 {len(hot_contents)} 个热点知识点")
    
    def _preload_knowledge_point(self, content: Dict[str, Any]):
        """预加载知识点内容"""
        content_id = content['id']
        
        # 缓存内容数据
        content_cache_data = {
            'question': content['question'] or '',
            'answer': content['answer'] or '',
            'type': content['type'] or 'fill',
            'difficulty': str(content['difficulty_level'] or 1),
            'explanation': content['explanation'] or ''
        }
        
        self.redis_master.hset(f"kp:content:{content_id}", mapping=content_cache_data)
        self.redis_master.expire(f"kp:content:{content_id}", 604800)  # 7天
        
        # 如果是选择题，加载选项
        if content['type'] == 'choice':
            choices = self._get_knowledge_point_choices(content_id)
            if choices:
                self.redis_master.setex(
                    f"kp:choices:{content_id}", 
                    604800, 
                    json.dumps(choices, ensure_ascii=False)
                )
    
    def _get_knowledge_point_choices(self, content_id: int) -> List[Dict[str, Any]]:
        """获取知识点选择题选项"""
        cursor = self.db_connection.cursor(dictionary=True)
        cursor.execute("""
            SELECT choice_key, choice_text, is_correct, explanation
            FROM knowledge_point_choices 
            WHERE content_id = %s 
            ORDER BY choice_key
        """, [content_id])
        
        choices = cursor.fetchall()
        cursor.close()
        
        return [
            {
                'key': choice['choice_key'],
                'text': choice['choice_text'],
                'is_correct': bool(choice['is_correct']),
                'explanation': choice['explanation'] or ''
            }
            for choice in choices
        ]
    
    def _warmup_system_configs(self):
        """预热系统配置"""
        logger.info("预热系统配置...")
        
        # 系统配置通常很少，可以全部加载
        system_configs = {
            'app_name': 'Memorin',
            'version': '2.0.0',
            'maintenance_mode': False,
            'max_daily_reviews': 200,
            'default_difficulty': 3,
            'review_intervals': [1, 3, 7, 15, 30, 90, 180, 365]
        }
        
        for key, value in system_configs.items():
            self.redis_master.setex(f"config:{key}", 86400, json.dumps(value))
    
    def _warmup_global_statistics(self):
        """预热全局统计"""
        logger.info("预热全局统计...")
        
        cursor = self.db_connection.cursor(dictionary=True)
        today = datetime.now().strftime('%Y-%m-%d')
        
        # 全局统计
        cursor.execute("""
            SELECT 
                COUNT(DISTINCT user_id) as active_users,
                COUNT(*) as total_reviews,
                AVG(CASE WHEN is_correct THEN 1 ELSE 0 END) as avg_accuracy
            FROM user_review_histories 
            WHERE DATE(reviewed_at) = %s
        """, [today])
        
        global_stats = cursor.fetchone()
        
        stats_data = {
            'total_reviews': str(global_stats['total_reviews'] or 0),
            'total_users_active': str(global_stats['active_users'] or 0),
            'avg_accuracy': f"{float(global_stats['avg_accuracy'] or 0):.2f}",
            'total_new_content': '0'  # 需要单独计算
        }
        
        self.redis_master.hset(f"stats:global:daily:{today}", mapping=stats_data)
        self.redis_master.expire(f"stats:global:daily:{today}", 86400)
        
        cursor.close()
    
    def _warmup_active_user_sessions(self):
        """预热活跃用户会话"""
        logger.info("预热活跃用户会话...")
        
        cursor = self.db_connection.cursor(dictionary=True)
        
        # 获取最近活跃的用户
        cursor.execute("""
            SELECT DISTINCT user_id
            FROM user_review_histories 
            WHERE reviewed_at >= %s
            LIMIT 100
        """, [datetime.now() - timedelta(hours=24)])
        
        active_users = cursor.fetchall()
        
        for user in active_users:
            self._preload_user_session_data(user['user_id'])
        
        cursor.close()
        logger.info(f"预热了 {len(active_users)} 个活跃用户会话")
    
    def _preload_user_session_data(self, user_id: int):
        """预加载用户会话数据"""
        cursor = self.db_connection.cursor(dictionary=True)
        
        # 用户基本信息
        cursor.execute("""
            SELECT id, username, email, status, last_login
            FROM users 
            WHERE id = %s
        """, [user_id])
        
        user_info = cursor.fetchone()
        if user_info:
            user_cache_data = {
                'id': str(user_info['id']),
                'username': user_info['username'],
                'email': user_info['email'],
                'status': user_info['status'],
                'last_login': user_info['last_login'].isoformat() if user_info['last_login'] else ''
            }
            
            self.redis_master.hset(f"user:info:{user_id}", mapping=user_cache_data)
            self.redis_master.expire(f"user:info:{user_id}", 86400)  # 24小时
        
        cursor.close()
    
    def cleanup_expired_cache(self):
        """清理过期和低价值缓存"""
        logger.info("开始清理过期缓存...")
        
        try:
            # 1. 清理过期的用户会话
            self._cleanup_expired_user_sessions()
            
            # 2. 清理低访问量的内容缓存
            self._cleanup_low_value_content_cache()
            
            # 3. 清理过期的统计缓存
            self._cleanup_expired_statistics()
            
            # 4. 内存整理
            self.redis_master.execute_command('MEMORY', 'PURGE')
            
            logger.info("缓存清理完成!")
            
        except Exception as e:
            logger.error(f"缓存清理失败: {e}")
            raise
    
    def _cleanup_expired_user_sessions(self):
        """清理过期用户会话"""
        logger.info("清理过期用户会话...")
        
        online_keys = self.redis_slave.keys('user:online:*')
        cleaned_count = 0
        
        for key in online_keys:
            last_activity = self.redis_slave.get(key)
            if last_activity:
                try:
                    last_time = float(last_activity.decode())
                    # 检查是否超过30分钟未活动
                    if time.time() - last_time > self.config['cache']['cleanup_expired_session_threshold']:
                        user_id = key.decode().split(':')[-1]
                        # 清理相关缓存
                        self.redis_master.delete(f"user:online:{user_id}")
                        self.redis_master.delete(f"user:session:{user_id}")
                        cleaned_count += 1
                except (ValueError, UnicodeDecodeError):
                    # 数据格式错误，直接删除
                    self.redis_master.delete(key)
                    cleaned_count += 1
        
        logger.info(f"清理了 {cleaned_count} 个过期用户会话")
    
    def _cleanup_low_value_content_cache(self):
        """清理低价值内容缓存"""
        logger.info("清理低价值内容缓存...")
        
        access_keys = self.redis_slave.keys('access:count:kp:*')
        cleaned_count = 0
        
        for key in access_keys:
            count = self.redis_slave.get(key)
            if count:
                try:
                    access_count = int(count.decode())
                    if access_count < self.config['cache']['cleanup_low_access_threshold']:
                        content_id = key.decode().split(':')[-1]
                        # 删除内容缓存
                        self.redis_master.delete(f"kp:content:{content_id}")
                        self.redis_master.delete(f"kp:choices:{content_id}")
                        cleaned_count += 1
                except (ValueError, UnicodeDecodeError):
                    pass
        
        logger.info(f"清理了 {cleaned_count} 个低访问量内容缓存")
    
    def _cleanup_expired_statistics(self):
        """清理过期统计缓存"""
        logger.info("清理过期统计缓存...")
        
        # 清理7天前的日统计数据
        cutoff_date = (datetime.now() - timedelta(days=7)).strftime('%Y-%m-%d')
        
        # 清理全局统计
        global_stats_keys = self.redis_slave.keys('stats:global:daily:*')
        cleaned_count = 0
        
        for key in global_stats_keys:
            key_str = key.decode()
            date_str = key_str.split(':')[-1]
            if date_str < cutoff_date:
                self.redis_master.delete(key)
                cleaned_count += 1
        
        # 清理知识库统计
        kb_stats_keys = self.redis_slave.keys('stats:kb:*:daily:*')
        for key in kb_stats_keys:
            key_str = key.decode()
            date_str = key_str.split(':')[-1]
            if date_str < cutoff_date:
                self.redis_master.delete(key)
                cleaned_count += 1
        
        logger.info(f"清理了 {cleaned_count} 个过期统计缓存")
    
    def collect_cache_metrics(self) -> Dict[str, Any]:
        """收集缓存指标"""
        try:
            info = self.redis_slave.info()
            
            metrics = {
                'memory_usage': info['used_memory'],
                'memory_usage_human': info['used_memory_human'],
                'memory_peak': info['used_memory_peak'],
                'memory_peak_human': info['used_memory_peak_human'],
                'connected_clients': info['connected_clients'],
                'total_commands_processed': info['total_commands_processed'],
                'instantaneous_ops_per_sec': info['instantaneous_ops_per_sec'],
                'keyspace_hits': info['keyspace_hits'],
                'keyspace_misses': info['keyspace_misses'],
                'evicted_keys': info['evicted_keys'],
                'expired_keys': info['expired_keys'],
            }
            
            # 计算命中率
            total_requests = info['keyspace_hits'] + info['keyspace_misses']
            if total_requests > 0:
                metrics['hit_rate'] = info['keyspace_hits'] / total_requests
            else:
                metrics['hit_rate'] = 0.0
            
            # 自定义指标
            metrics.update({
                'hotspot_keys_count': self.redis_slave.zcard('hotspot:kp:daily'),
                'active_users_count': len(self.redis_slave.keys('user:online:*')),
                'cache_layers_stats': self._get_cache_layers_stats(),
                'timestamp': datetime.now().isoformat()
            })
            
            return metrics
            
        except Exception as e:
            logger.error(f"收集缓存指标失败: {e}")
            return {}
    
    def _get_cache_layers_stats(self) -> Dict[str, int]:
        """获取各缓存层统计"""
        stats = {}
        
        try:
            # L1 热点缓存
            l1_keys = self.redis_slave.keys('hotspot:*')
            stats['l1_hotspot'] = len(l1_keys)
            
            # L2 会话缓存
            l2_keys = self.redis_slave.keys('user:info:*')
            stats['l2_session'] = len(l2_keys)
            
            # L3 内容缓存
            l3_keys = self.redis_slave.keys('kp:content:*')
            stats['l3_content'] = len(l3_keys)
            
            # L4 统计缓存
            l4_keys = self.redis_slave.keys('stats:*')
            stats['l4_statistics'] = len(l4_keys)
            
        except Exception as e:
            logger.error(f"获取缓存层统计失败: {e}")
            stats = {'l1_hotspot': 0, 'l2_session': 0, 'l3_content': 0, 'l4_statistics': 0}
        
        return stats
    
    def verify_cache_consistency(self) -> List[Dict[str, Any]]:
        """验证缓存与数据库的一致性"""
        logger.info("开始缓存一致性检查...")
        
        inconsistencies = []
        
        try:
            # 检查知识库信息一致性
            inconsistencies.extend(self._check_knowledge_base_consistency())
            
            # 检查用户状态一致性
            inconsistencies.extend(self._check_user_state_consistency())
            
            if inconsistencies:
                logger.warning(f"发现 {len(inconsistencies)} 个不一致问题")
            else:
                logger.info("缓存一致性检查通过!")
            
        except Exception as e:
            logger.error(f"缓存一致性检查失败: {e}")
            inconsistencies.append({
                'type': 'check_error',
                'error': str(e)
            })
        
        return inconsistencies
    
    def _check_knowledge_base_consistency(self) -> List[Dict[str, Any]]:
        """检查知识库信息一致性"""
        inconsistencies = []
        kb_keys = self.redis_slave.keys('kb:info:*')
        
        for key in kb_keys[:20]:  # 检查前20个
            kb_id = key.decode().split(':')[-1]
            cached_data = self.redis_slave.hgetall(key)
            
            # 从数据库获取数据
            cursor = self.db_connection.cursor(dictionary=True)
            cursor.execute("""
                SELECT id, name, owner_id, visibility, content_count, subscriber_count
                FROM knowledge_bases WHERE id = %s
            """, [kb_id])
            
            db_data = cursor.fetchone()
            cursor.close()
            
            if db_data:
                # 比较关键字段
                if (cached_data.get(b'name', b'').decode() != db_data['name'] or
                    cached_data.get(b'subscriber_count', b'').decode() != str(db_data['subscriber_count'])):
                    inconsistencies.append({
                        'type': 'knowledge_base',
                        'id': kb_id,
                        'issue': 'data_mismatch',
                        'cached': {k.decode(): v.decode() for k, v in cached_data.items()},
                        'database': db_data
                    })
        
        return inconsistencies
    
    def _check_user_state_consistency(self) -> List[Dict[str, Any]]:
        """检查用户状态一致性"""
        inconsistencies = []
        state_keys = self.redis_slave.keys('user:kp:state:*')
        
        # 随机抽样检查
        sample_keys = random.sample(state_keys, min(50, len(state_keys)))
        
        for key in sample_keys:
            parts = key.decode().split(':')
            if len(parts) >= 5:
                user_id, content_id = parts[3], parts[4]
                cached_data = self.redis_slave.hgetall(key)
                
                # 从数据库获取数据
                cursor = self.db_connection.cursor(dictionary=True)
                cursor.execute("""
                    SELECT mastery_level, review_count, correct_count, 
                           consecutive_correct, last_reviewed, next_review
                    FROM user_knowledge_point_states 
                    WHERE user_id = %s AND content_id = %s
                """, [user_id, content_id])
                
                db_data = cursor.fetchone()
                cursor.close()
                
                if db_data:
                    # 比较关键字段
                    if (cached_data.get(b'mastery_level', b'').decode() != str(db_data['mastery_level']) or
                        cached_data.get(b'review_count', b'').decode() != str(db_data['review_count'])):
                        inconsistencies.append({
                            'type': 'user_state',
                            'user_id': user_id,
                            'content_id': content_id,
                            'issue': 'data_mismatch'
                        })
        
        return inconsistencies
    
    def update_access_counter(self, resource_type: str, resource_id: int):
        """更新访问计数器"""
        key = f"access:count:{resource_type}:{resource_id}"
        
        # 增加计数
        current_count = self.redis_master.incr(key)
        
        # 设置过期时间
        if current_count == 1:
            self.redis_master.expire(key, self.config['cache']['access_counter_ttl'])
        
        # 更新热点排行
        if current_count % self.config['cache']['access_counter_batch_size'] == 0:
            self._update_hotspot_ranking(resource_type, resource_id, current_count)
    
    def _update_hotspot_ranking(self, resource_type: str, resource_id: int, score: int):
        """更新热点排行"""
        hotspot_key = f"hotspot:{resource_type}:daily"
        
        # 添加到有序集合
        self.redis_master.zadd(hotspot_key, {str(resource_id): score})
        
        # 保持排行榜大小 (只保留前1000)
        self.redis_master.zremrangebyrank(hotspot_key, 0, -1001)
        
        # 设置过期时间
        self.redis_master.expire(hotspot_key, 86400)  # 24小时
    
    def _compress_large_object(self, data: Any) -> str:
        """压缩大型JSON对象"""
        json_str = json.dumps(data, ensure_ascii=False)
        
        if len(json_str) > 1024:  # 大于1KB的对象进行压缩
            compressed = gzip.compress(json_str.encode('utf-8'))
            return base64.b64encode(compressed).decode('ascii')
        
        return json_str
    
    def close(self):
        """关闭连接"""
        if self.db_connection:
            self.db_connection.close()
        logger.info("缓存管理器已关闭")

def load_config() -> Dict[str, Any]:
    """加载配置"""
    config = {
        'redis': {
            'master': {
                'host': os.getenv('REDIS_MASTER_HOST', 'localhost'),
                'port': int(os.getenv('REDIS_MASTER_PORT', 6379)),
                'password': os.getenv('REDIS_MASTER_PASSWORD')
            },
            'slave': {
                'host': os.getenv('REDIS_SLAVE_HOST', 'localhost'),
                'port': int(os.getenv('REDIS_SLAVE_PORT', 6379)),
                'password': os.getenv('REDIS_SLAVE_PASSWORD')
            }
        },
        'database': {
            'host': os.getenv('DB_HOST', 'localhost'),
            'port': int(os.getenv('DB_PORT', 3306)),
            'user': os.getenv('DB_USER', 'root'),
            'password': os.getenv('DB_PASSWORD', ''),
            'name': os.getenv('DB_NAME', 'memorin')
        },
        'cache': {
            'warmup_popular_kb_count': 20,
            'warmup_hot_content_count': 50,
            'cleanup_low_access_threshold': 5,
            'cleanup_expired_session_threshold': 1800,
            'access_counter_ttl': 3600,
            'access_counter_batch_size': 10
        }
    }
    
    return config

def main():
    """主函数"""
    parser = argparse.ArgumentParser(description='Memorin Redis缓存管理工具')
    parser.add_argument('--warmup', action='store_true', help='执行缓存预热')
    parser.add_argument('--cleanup', action='store_true', help='执行缓存清理')
    parser.add_argument('--monitor', action='store_true', help='显示监控指标')
    parser.add_argument('--check', action='store_true', help='执行一致性检查')
    parser.add_argument('--config', type=str, help='配置文件路径')
    
    args = parser.parse_args()
    
    # 加载配置
    config = load_config()
    
    # 创建缓存管理器
    cache_manager = RedisCacheManager(config)
    
    try:
        if args.warmup:
            cache_manager.warmup_cache()
        
        elif args.cleanup:
            cache_manager.cleanup_expired_cache()
        
        elif args.monitor:
            metrics = cache_manager.collect_cache_metrics()
            print(json.dumps(metrics, indent=2, ensure_ascii=False))
        
        elif args.check:
            inconsistencies = cache_manager.verify_cache_consistency()
            if inconsistencies:
                print(json.dumps(inconsistencies, indent=2, ensure_ascii=False))
            else:
                print("缓存一致性检查通过")
        
        else:
            parser.print_help()
    
    finally:
        cache_manager.close()

if __name__ == '__main__':
    main() 