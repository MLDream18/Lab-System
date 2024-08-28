//节流实现
export default function throttle(fn: Function, delay: number) {
    //有效标志，用来判断是否可以允许下一次执行，true表示允许，false表示禁止
    let flag = true
    //借助闭包，使得变量flag不被回收
    return function (this: any) {
        //保存当前环境下的实例对象，this即为引入该方法的那个组件实例对象
        let th = this;
        //保存传入参数
        let args = arguments;
        //如果flag为false，不允许执行fn()
        if (!flag) {
            //未超过时间间隔，flag无效，不执行fn()
            return false
        }
        fn.apply(th, args)
        //在时间间隔内把状态位flag设为禁止（false）
        flag = false
        setTimeout(() => {
            //超过时间间隔把状态位flag设为允许（true）,这样下次就能执行fn()了
            flag = true
        }, delay)
    }
}