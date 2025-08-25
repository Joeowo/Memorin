// 通知类型
export type NotificationType = 'success' | 'error' | 'warning' | 'info'

// 通知选项
export interface NotificationOptions {
  title?: string
  message: string
  type?: NotificationType
  duration?: number
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center'
  closable?: boolean
  persistent?: boolean
}

// 通知实例
interface NotificationInstance {
  id: string
  element: HTMLElement
  timer?: number
  options: NotificationOptions
}

// 全局通知管理器
class NotificationManager {
  private notifications: NotificationInstance[] = []
  private idCounter = 0

  // 显示通知
  show(options: NotificationOptions): string {
    const id = `notification-${++this.idCounter}`
    
    const finalOptions: NotificationOptions = {
      type: 'info',
      duration: 3000,
      position: 'top-right',
      closable: true,
      persistent: false,
      ...options
    }

    const element = this.createNotificationElement(id, finalOptions)
    
    const notification: NotificationInstance = {
      id,
      element,
      options: finalOptions
    }

    this.notifications.push(notification)
    document.body.appendChild(element)

    // 添加滑入动画
    element.style.animation = 'slideInRight 0.3s ease'

    // 自动关闭
    if (finalOptions.duration! > 0 && !finalOptions.persistent) {
      notification.timer = window.setTimeout(() => {
        this.close(id)
      }, finalOptions.duration)
    }

    return id
  }

  // 创建通知元素
  private createNotificationElement(id: string, options: NotificationOptions): HTMLElement {
    const notification = document.createElement('div')
    notification.id = id
    notification.className = `base-notification base-notification--${options.type} base-notification--${options.position}`
    
    // 设置样式
    notification.style.cssText = `
      position: fixed;
      z-index: 2000;
      padding: 1rem 1.5rem;
      border-radius: 12px;
      color: white;
      font-weight: 500;
      max-width: 400px;
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: flex-start;
      gap: 1rem;
      cursor: pointer;
      transition: all 0.3s ease;
      ${this.getPositionStyles(options.position || 'top-right')}
      ${this.getVariantStyles(options.type || 'info')}
    `

    // 创建内容容器
    const content = document.createElement('div')
    content.style.cssText = `
      flex: 1;
      min-width: 0;
    `

    // 标题
    if (options.title) {
      const title = document.createElement('div')
      title.textContent = options.title
      title.style.cssText = `
        font-weight: 600;
        font-size: 1rem;
        color: white;
        margin-bottom: 0.25rem;
        line-height: 1.3;
      `
      content.appendChild(title)
    }

    // 消息
    const message = document.createElement('div')
    message.textContent = options.message
    message.style.cssText = `
      font-size: 0.875rem;
      color: rgba(255, 255, 255, 0.9);
      line-height: 1.4;
      word-wrap: break-word;
    `
    content.appendChild(message)

    notification.appendChild(content)

    // 关闭按钮
    if (options.closable) {
      const closeBtn = document.createElement('button')
      closeBtn.textContent = '✕'
      closeBtn.style.cssText = `
        flex-shrink: 0;
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        width: 1.5rem;
        height: 1.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.75rem;
        color: white;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-top: 0.125rem;
      `
      
      closeBtn.addEventListener('click', (e) => {
        e.stopPropagation()
        this.close(id)
      })

      closeBtn.addEventListener('mouseenter', () => {
        closeBtn.style.background = 'rgba(255, 255, 255, 0.3)'
        closeBtn.style.borderColor = 'rgba(255, 255, 255, 0.5)'
        closeBtn.style.transform = 'scale(1.1)'
      })

      closeBtn.addEventListener('mouseleave', () => {
        closeBtn.style.background = 'rgba(255, 255, 255, 0.2)'
        closeBtn.style.borderColor = 'rgba(255, 255, 255, 0.3)'
        closeBtn.style.transform = 'scale(1)'
      })

      notification.appendChild(closeBtn)
    }

    // 悬停效果
    notification.addEventListener('mouseenter', () => {
      notification.style.transform = 'translateY(-2px)'
      notification.style.boxShadow = '0 12px 40px rgba(0, 0, 0, 0.15)'
    })

    notification.addEventListener('mouseleave', () => {
      notification.style.transform = 'translateY(0)'
      notification.style.boxShadow = '0 8px 32px rgba(0, 0, 0, 0.1)'
    })

    return notification
  }

  // 获取位置样式
  private getPositionStyles(position: string): string {
    switch (position) {
      case 'top-right':
        return 'top: 140px; right: 20px;'
      case 'top-left':
        return 'top: 140px; left: 20px;'
      case 'bottom-right':
        return 'bottom: 20px; right: 20px;'
      case 'bottom-left':
        return 'bottom: 20px; left: 20px;'
      case 'top-center':
        return 'top: 140px; left: 50%; transform: translateX(-50%);'
      case 'bottom-center':
        return 'bottom: 20px; left: 50%; transform: translateX(-50%);'
      default:
        return 'top: 140px; right: 20px;'
    }
  }

  // 获取变体样式
  private getVariantStyles(type: string): string {
    switch (type) {
      case 'success':
        return 'background: linear-gradient(135deg, rgba(46, 204, 113, 0.9), rgba(39, 174, 96, 0.9));'
      case 'error':
        return 'background: linear-gradient(135deg, rgba(231, 76, 60, 0.9), rgba(192, 57, 43, 0.9));'
      case 'warning':
        return 'background: linear-gradient(135deg, rgba(243, 156, 18, 0.9), rgba(230, 126, 34, 0.9));'
      case 'info':
        return 'background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));'
      default:
        return 'background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));'
    }
  }

  // 关闭通知
  close(id: string) {
    const index = this.notifications.findIndex(n => n.id === id)
    if (index === -1) return

    const notification = this.notifications[index]
    
    // 清除定时器
    if (notification.timer) {
      clearTimeout(notification.timer)
    }

    // 添加滑出动画
    notification.element.style.animation = 'slideOutRight 0.3s ease'
    
    // 动画完成后移除元素
    setTimeout(() => {
      if (notification.element.parentNode) {
        notification.element.parentNode.removeChild(notification.element)
      }
    }, 300)

    // 从数组中移除
    this.notifications.splice(index, 1)
  }

  // 关闭所有通知
  closeAll() {
    this.notifications.forEach(notification => {
      this.close(notification.id)
    })
  }

  // 快捷方法
  success(message: string, title?: string, options?: Partial<NotificationOptions>) {
    return this.show({
      type: 'success',
      message,
      title,
      ...options
    })
  }

  error(message: string, title?: string, options?: Partial<NotificationOptions>) {
    return this.show({
      type: 'error',
      message,
      title,
      ...options
    })
  }

  warning(message: string, title?: string, options?: Partial<NotificationOptions>) {
    return this.show({
      type: 'warning',
      message,
      title,
      ...options
    })
  }

  info(message: string, title?: string, options?: Partial<NotificationOptions>) {
    return this.show({
      type: 'info',
      message,
      title,
      ...options
    })
  }
}

// 添加必要的CSS动画
const style = document.createElement('style')
style.textContent = `
  @keyframes slideInRight {
    from { 
      transform: translateX(100%) scale(0.95); 
      opacity: 0; 
    }
    to { 
      transform: translateX(0) scale(1); 
      opacity: 1; 
    }
  }

  @keyframes slideOutRight {
    from { 
      transform: translateX(0) scale(1); 
      opacity: 1; 
    }
    to { 
      transform: translateX(100%) scale(0.95); 
      opacity: 0; 
    }
  }
`
document.head.appendChild(style)

// 创建全局实例
const notificationManager = new NotificationManager()

// 导出简化的API
export const showNotification = (options: NotificationOptions) => notificationManager.show(options)
export const closeNotification = (id: string) => notificationManager.close(id)
export const closeAllNotifications = () => notificationManager.closeAll()

// 快捷方法
export const notifySuccess = (message: string, title?: string, options?: Partial<NotificationOptions>) => 
  notificationManager.success(message, title, options)

export const notifyError = (message: string, title?: string, options?: Partial<NotificationOptions>) => 
  notificationManager.error(message, title, options)

export const notifyWarning = (message: string, title?: string, options?: Partial<NotificationOptions>) => 
  notificationManager.warning(message, title, options)

export const notifyInfo = (message: string, title?: string, options?: Partial<NotificationOptions>) => 
  notificationManager.info(message, title, options)

// 默认导出
export default notificationManager 