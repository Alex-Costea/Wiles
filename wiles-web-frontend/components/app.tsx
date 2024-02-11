import {useState} from "react";
import Cookies from 'js-cookie';


function App() {
    function getDomain()
    {
        let domain = window.location.protocol + "//" + window.location.hostname
        if(window.location.protocol === "http:")
            domain += ":80"
        else if(window.location.protocol === "http:")
            domain += ":443"
        else throw Error("Unknown protocol")
        return domain
    }

    const [output, setOutput] = useState({response: '', errors: ''})
    const [code, setCode] = useState(`let name := read_line()
writeline("Hello, " + name + "!")`)
    const [input, setInput] = useState("Wiles")

    async function GetXSRF()
    {
        if(Cookies.get("XSRF-TOKEN") === undefined)
        {
            await fetch(`${getDomain()}/run`, {
                method: 'PUT',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
            return Cookies.get("XSRF-TOKEN")!
        }
        return Cookies.get("XSRF-TOKEN")!
    }

    function Submit(e : any)
    {
        e.preventDefault()
        GetXSRF().then(xsrf => {
            fetch(`${getDomain()}/run`, {
                method: 'PUT',
                headers: {
                    'X-XSRF-TOKEN' : xsrf,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    code: code,
                    input: input
                })
            }).then(response => response.json()).then(response => {
                setOutput(response)
            })
        })
    }

    return (
        <div className="App">
            <header className="App-header">
                <img src="/logo2.svg" className="App-logo" alt="logo"/>
            </header>
            <main>
                <div id={"column1"}>
                    <form onSubmit={Submit}>
                        <p>
                            <label htmlFor="code">Code:</label>
                        </p>
                        <p>
                        <textarea id="code" rows={10} cols={40} value={code}
                                  onInput={e => setCode((e.target as HTMLTextAreaElement).value)}/>
                        </p>
                        <p>
                            <label htmlFor="input">Input:</label>
                        </p>
                        <p>
                        <textarea id="input" rows={10} cols={40} value={input}
                                  onInput={e => setInput((e.target as HTMLTextAreaElement).value)}/>
                        </p>
                        <p>
                            <input type="submit" id={"submit"}></input>
                        </p>
                    </form>
                </div>
                <div id={"column2"}>
                    <p>
                        <label htmlFor="output">Output:</label>
                    </p>
                    <p>
                        <textarea disabled id="output" rows={10} cols={40} value={output.response}></textarea>
                    </p>
                    <p>
                        <label htmlFor="errors">Errors:</label>
                    </p>
                    <p>
                        <textarea disabled id="errors" rows={10} cols={40} value={output.errors}></textarea>
                    </p>
                    <p><a href={"https://alex.costea.in/Wiles/"}>Learn more about Wiles.</a></p>
                </div>
            </main>
        </div>
    );
}

export default App;
