import {Dispatch, FormEvent, useEffect, useReducer, useRef} from "react";
import Cookies from 'js-cookie';
import {ContentEditableEvent} from "react-contenteditable";
import Field from '../field/field.tsx'
import SubmitCode from "../submitCode/submitCode.tsx";
import MoreInfo from "../moreInfo/moreInfo.tsx";
import './app.css';

interface locationFormat {
    line : number, lineIndex : number, lineEnd : number, lineEndIndex : number
}

interface errorFormat {
    message: string,
    location: locationFormat
}

interface responseFormat{
    response : string, errorList : errorFormat[]
}

interface lineValue{
    line : number, lineIndex : number
}

interface syntaxFormat{
    type: string, location: locationFormat
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

    async function getSyntax(code: string) : Promise<string> {
        return (await getXSRF().then(xsrf => {
            const codeNoAnnotations = getCodeNoAnnotations(code)
            return fetch(`${getDomain()}/syntax`, {
                method: 'PUT',
                headers: {
                    'X-XSRF-TOKEN': xsrf,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    code: codeNoAnnotations,
                    input: input
                })
            }).then(response => response.text()).then((response => {
                return response
            }))
        }))!
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
                async (response: responseFormat) => {
                    let output = response.response
                    const errorList = response.errorList
                    const annotatedCode = addAnnotationsToCode(codeNoAnnotations, errorList)
                    setCode(await annotatedCode)
                    const globalErrors = getGlobalErrors(errorList)
                    if (globalErrors.length > 0) {
                        for (const error of globalErrors) {
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

    async function addAnnotationsToCode(code : string, errorList : errorFormat[]) : Promise<string>
    {
        let line = 1
        let lineIndex = 1
        let newCode = ""

        //get errors
        const errorStart = new Map<string, string>()
        const errorEnd = new Map<string, string>()
        for(const error of errorList)
        {
            const lineStart = {line : error.location.line, lineIndex : error.location.lineIndex} as lineValue
            const lineEnd = {line : error.location.lineEnd, lineIndex : error.location.lineEndIndex} as lineValue
            if (lineStart.line !== -1) {
                errorStart.set(JSON.stringify(lineStart), error.message)
                errorEnd.set(JSON.stringify(lineEnd), error.message)
            }
        }

        //get syntax
        const syntaxStart = new Map<string, string>()
        const syntaxEnd = new Map<string, string>()
        const syntaxCode = JSON.parse(await getSyntax(code)) as syntaxFormat[]
        for(const syntax of syntaxCode)
        {
            const type = syntax.type.toLowerCase()
            const lineStart = {line : syntax.location.line, lineIndex : syntax.location.lineIndex} as lineValue
            const lineEnd = {line : syntax.location.lineEnd, lineIndex : syntax.location.lineEndIndex} as lineValue
            syntaxStart.set(JSON.stringify(lineStart), type)
            syntaxEnd.set(JSON.stringify(lineEnd), type)
        }

        for(const character of code)
        {
            const currentLine = JSON.stringify({line : line, lineIndex: lineIndex} as lineValue)
            if(errorStart.has(currentLine))
            {
                const message = errorStart.get(currentLine)!
                newCode += `<span aria-invalid="true" aria-errormessage="${message}" class="error">`
            }
            if(syntaxStart.has(currentLine))
            {
                const syntax = syntaxStart.get(currentLine)!
                newCode += `<span class="syntax_${syntax}">`
            }
            if(syntaxEnd.has(currentLine))
            {
                newCode += `</span>`
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
