import {Dispatch, FormEvent, useEffect, useReducer, useRef} from "react";
import Cookies from 'js-cookie';
import {ContentEditableEvent} from "react-contenteditable";
import Field from '../field/field.tsx'
import SubmitCode from "../submitCode/submitCode.tsx";
import MoreInfo from "../moreInfo/moreInfo.tsx";
import './app.css';

interface errorLocationFormat {
    line : number, lineIndex : number, lineEnd : number, lineEndIndex : number
}

interface errorFormat {
    message: string,
    location: errorLocationFormat
}

interface responseFormat{
    response : string, errorList : errorFormat[]
}

interface lineValue{
    line : number, lineIndex : number
}

function usePersistedState(keyName : string, defaultValue : string) : [string, Dispatch<string>]
{
    const keyNameRef = useRef(keyName)
    const defaultValueRef = useRef(defaultValue)

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
        const storedState = window.localStorage.getItem(keyNameRef.current)
        setState(storedState ?? defaultValueRef.current)
    },[])

    return [state, setState]
}

function getDomain()
{
    let domain = window.location.protocol + "//" + window.location.hostname
    if(window.location.protocol === "http:")
        domain += ":8080"
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
    const [code, setCode] = usePersistedState("code",
        'let name := read_line()\nwrite_line("Hello, " + name + "!")')
    const [input, setInput] = usePersistedState("input", "Wiles")

    function getSyntax(code : string)
    {
        getXSRF().then(xsrf =>
        {
            const codeNoAnnotations = getCodeNoAnnotations(code)
            fetch(`${getDomain()}/syntax`, {
                method: 'PUT',
                headers: {
                    'X-XSRF-TOKEN' : xsrf,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    code: codeNoAnnotations,
                    input: input
                })
            }).then(response => response.text()).then((response => {
                console.log(response)
            }))
        })
    }

    function submit(e : FormEvent<HTMLFormElement>)
    {
        e.preventDefault()
        const codeNoAnnotations = getCodeNoAnnotations(code)
        getXSRF().then(xsrf => {
            fetch(`${getDomain()}/run`, {
                method: 'PUT',
                headers: {
                    'X-XSRF-TOKEN' : xsrf,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    code: codeNoAnnotations,
                    input: input
                })
            }).then(response => response.json()).then(
                (response : responseFormat)  => {
                    let output = response.response
                    const errorList = response.errorList
                    const annotatedCode = addAnnotationsToCode(codeNoAnnotations, errorList)
                    setCode(annotatedCode)
                    const globalErrors = getGlobalErrors(errorList)
                    if(globalErrors.length > 0)
                    {
                        for(const error of globalErrors)
                        {
                            output += `<span class="globalError">` + error.message + `</span>\n`
                        }
                    }
                    setOutput(output)
                })
        })
    }

    function getGlobalErrors(errorList : errorFormat[]) : errorFormat[]
    {
        let globalErrors : errorFormat[] = []
        for(const error of errorList)
        {
            const lineStart = {line : error.location.line, lineIndex : error.location.lineIndex} as lineValue
            if (lineStart.line === -1) {
                globalErrors.push(error)
            }
        }
        return globalErrors
    }

    function getCodeNoAnnotations(value : string)
    {
        const parser = new DOMParser();
        const doc = parser.parseFromString(
            `<div id="elem"></div>`, "text/html")
        const element =  doc.getElementById("elem")!
        element.innerHTML = value
        return element.innerText
    }

    function addAnnotationsToCode(code : string, errorList : errorFormat[]) : string
    {
        let line = 1
        let lineIndex = 1
        const errorStart = new Map<string, string>()
        const errorEnd = new Map<string, string>()
        let newCode = ""
        for(const error of errorList)
        {
            const lineStart = {line : error.location.line, lineIndex : error.location.lineIndex} as lineValue
            const lineEnd = {line : error.location.lineEnd, lineIndex : error.location.lineEndIndex} as lineValue
            if (lineStart.line !== -1) {
                errorStart.set(JSON.stringify(lineStart), error.message)
                errorEnd.set(JSON.stringify(lineEnd), error.message)
            }
        }
        for(const character of code)
        {
            const currentLine = JSON.stringify({line : line, lineIndex: lineIndex} as lineValue)
            if(errorStart.has(currentLine))
            {
                const message = errorStart.get(currentLine)!
                newCode += `<span aria-invalid="true" aria-errormessage="${message}" class="error">`
            }
            if(errorEnd.has(currentLine))
            {
                newCode += `</span>`
            }
            newCode += character
            if(character === "\n")
            {
                line += 1
                lineIndex = 1
            }
            else{
                lineIndex += 1
            }
        }
        return newCode
    }

    const onInputChange = (e: ContentEditableEvent) => setInput((e.target as HTMLTextAreaElement).value)

    function onCodeChange(e : ContentEditableEvent)
    {
        const value = e.target.value
        setCode(value)
        getSyntax(value)
    }

    return <div id={"App"}>
        <main>
            <div id="column1" className={"column"}>
                <form onSubmit={submit} className={"form"} id={"form"}>
                    <Field label="Code:" id="code" onChange={onCodeChange} innerHTML={code}/>
                </form>
            </div>
            <div id="column2" className={"column"}>
                <Field label="Input:" id="input" onChange={onInputChange} innerHTML={input}/>
                <Field label="Output:" id="output" innerHTML={output} disabled/>
            </div>
        </main>
        <div id={"extra"}>
            <SubmitCode/>
            <MoreInfo/>
        </div>
    </div>
}

export default App;
