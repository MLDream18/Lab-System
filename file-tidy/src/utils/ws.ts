import debounce from "lodash/debounce"

class MyWebSocket extends WebSocket {
    constructor(url: string, protocols?: string | string[]) {
        super(url, protocols)
    }
    init(url: string, protocols?: string | string[]): WebSocket {
        return new MyWebSocket(url, protocols)
    }
}

const debounceWs = debounce((url: string) => {
    return new MyWebSocket(url, `${localStorage.getItem("token")}`)
}, 300)

const init = (url: string) => {
    return new MyWebSocket(url, `${localStorage.getItem("token")}`)
}

export { debounceWs, init }