import {Dispatch, FormEvent, useEffect, useReducer} from "react";
import Cookies from 'js-cookie';
import Image from 'next/image'
import ContentEditable, {ContentEditableEvent} from "react-contenteditable";

interface responseFormat{
    response : string, errors : string
}

function usePersistedState(keyName : string, defaultValue : string)
{
    function persistState(keyName : string)
    {
        return function(_prevState : string, value : string)
        {
            window.localStorage.setItem(keyName, value)
            return value
        }
    }

    const [state, setState] = useReducer(persistState(keyName),"")

    useEffect(()=>{
        const storedState = window.localStorage.getItem(keyName)
        if(storedState !== null)
        {
            setState(storedState)
        }
        else
        {
            setState(defaultValue)
        }
    },[])

    return [state, setState] as [string, Dispatch<string>]
}

function App() {
    function getDomain()
    {
        let domain = window.location.protocol + "//" + window.location.hostname
        if(window.location.protocol === "http:")
            domain += ":80"
        else if(window.location.protocol === "https:")
            domain += ":443"
        else throw Error("Unknown protocol")
        return domain
    }

    const [output, setOutput] = usePersistedState("output", "")
    const [errors, setErrors] = usePersistedState("errors", "")
    const [code, setCode] = usePersistedState("code",
        'let name := read_line()\nwrite_line("Hello, " + name + "!")')
    const [input, setInput] = usePersistedState("input", "Wiles")

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

    function Submit(e : FormEvent<HTMLFormElement>)
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
            }).then(response => response.json()).then(
                (response : responseFormat)  => {
                setOutput(response.response)
                setErrors(response.errors)
            })
        })
    }

    function onCodeChange(e : ContentEditableEvent)
    {
        const value = e.target.value
        const parser = new DOMParser();
        const doc = parser.parseFromString(
            `<div id="elem"></div>`, "text/html")
        const element =  doc.getElementById("elem")!
        element.innerHTML = value
        const newValue = element.innerText
        setCode(newValue)
    }

    return (
        <div className="App">
            <div className={"background"}>
                <Image src="images/background.png" alt={"background"} fill={true} priority style={{objectFit: "cover"}}></Image>
            </div>
            <header className="App-header">
                <Image src="logo_pastel.svg" width={500} height={500} className="App-logo" alt="Wiles logo" priority/>
            </header>
            <main>
                <div id={"column1"}>
                    <form onSubmit={Submit}>
                        <p>
                            <label htmlFor="code">Code:</label>
                        </p>
                        <p>
                        <ContentEditable id="code" className={"textarea"} tagName={"span"} spellCheck={false}
                                  onChange={onCodeChange} html={code}/>
                        </p>
                        <p>
                            <label htmlFor="input">Input:</label>
                        </p>
                        <p>
                        <textarea id="input" value={input}
                                  onInput={e => setInput((e.target as HTMLTextAreaElement).value)}/>
                        </p>
                        <p>
                            <input type="submit" id={"submit"} value={"Run Code"}></input>
                        </p>
                    </form>
                </div>
                <div id={"column2"}>
                    <p>
                        <label htmlFor="output">Output:</label>
                    </p>
                    <p>
                        <textarea disabled id="output" value={output}></textarea>
                    </p>
                    <p>
                        <label htmlFor="errors">Errors:</label>
                    </p>
                    <p>
                        <textarea disabled id="errors" value={errors}></textarea>
                    </p>
                    <p><a href={"https://alex.costea.in/Wiles/"}>Learn more about Wiles.</a></p>
                </div>
            </main>
        </div>
    );
}

export default App;
