import {Dispatch, FormEvent, useEffect, useReducer} from "react";
import Cookies from 'js-cookie';
import Image from 'next/image'
import {ContentEditableEvent} from "react-contenteditable";
import Field from './field'

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

async function getXSRF()
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


function App() {

    const [output, setOutput] = usePersistedState("output", "")
    const [errors, setErrors] = usePersistedState("errors", "")
    const [code, setCode] = usePersistedState("code",
        'let name := read_line()\nwrite_line("Hello, " + name + "!")')
    const [input, setInput] = usePersistedState("input", "Wiles")

    function submit(e : FormEvent<HTMLFormElement>)
    {
        e.preventDefault()
        getXSRF().then(xsrf => {
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

    const onInputChange = (e: ContentEditableEvent) => setInput((e.target as HTMLTextAreaElement).value)

    return (
        <div className="App">
            <div className="background">
                <Image src="images/background.png"
                       alt="background" fill={true} priority style={{objectFit: "cover"}}></Image>
            </div>
            <header className="App-header">
                <Image src="logo_pastel.svg" width={500} height={500} className="App-logo" alt="Wiles logo" priority/>
            </header>
            <main>
                <div id="column1">
                    <form onSubmit={submit}>
                        <Field label="Code:" id="code" onChange={onCodeChange} innerHTML={code}/>
                        <Field label="Input:" id="input" onChange={onInputChange} innerHTML={input}/>
                        <p>
                            <input type="submit" id="submit" value="Run Code"></input>
                        </p>
                    </form>
                </div>
                <div id="column2">
                    <Field label="Output:" id="output" innerHTML={output} disabled/>
                    <Field label="Errors:" id="errors" innerHTML={errors} disabled/>
                    <p><a href="https://alex.costea.in/Wiles/">Learn more about Wiles.</a></p>
                </div>
            </main>
        </div>
    );
}

export default App;
