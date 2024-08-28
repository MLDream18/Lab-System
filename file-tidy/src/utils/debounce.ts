// 防抖函数
export default function debounce(func: Function, delay: number) { // 这里的func是防抖要执行的函数，delay是延迟时间
    let timer: any = null;
    // 借助闭包，将timer和func作为参数传入
    return function (this: any) {
        // 保存当前环境下的实例对象，this为引入该函数的实例对象
        let th = this;

        // 保存传入的参数
        let args = arguments;
        // 第一次的timer为空，跳过该判断，执行setTimeout
        if(timer) {
            // 如果timer存在，则清除之前的setTimeout
            clearTimeout(timer);
        }
        timer = setTimeout(() => {
            //apply()，改变this指向，指向正在操作的组件实例，传入参数
            func.apply(th, args);
            timer = null;
        }, delay);
    };
}


//由于使用闭包，使得timer不被回收，在A组件中每次调用该方法时会去判断timer是否存在，如果存在，
//表示上一次输入在等待执行fn()，期间如果继续输入，且在1s内，则会把上一次未执行的
//（setTimeout中的）fn()销毁，重新定时1s，以此来达到输入结束过1s才执行fn()，即触发事件或者发送
//请求。