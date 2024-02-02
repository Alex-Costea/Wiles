import logo from './logo.svg';
import './App.css';
import {useState} from "react";
import Cookies from 'js-cookie';


function App() {

    const [output, setOutput] = useState({response: '', errors: ''})
    const [code, setCode] = useState(`let name := read_line()
writeline("Hello, " + name + "!")`)
    const [input, setInput] = useState("Wiles")
    const csfr = Cookies.get("XSRF-TOKEN")

    function Submit(e)
    {
        e.preventDefault()
        fetch("run", {
            method: 'PUT',
            headers: {
                'X-XSRF-TOKEN' : csfr,
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
    }

    return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
      </header>
        <form onSubmit={Submit}>
            <p>
                <label htmlFor="code">Code:</label>
            </p>
            <p>
                <textarea id="code" rows="10" cols="40" value={code} onInput={e=> setCode(e.target.value)} />
            </p>
            <p>
                <label htmlFor="input">Input:</label>
            </p>
            <p>
                <textarea id="input" rows="10" cols="40" value={input} onInput={e=> setInput(e.target.value)} />
            </p>
            <p>
                <input type="submit"></input>
            </p>
        </form>
        <p>
            <label htmlFor="output">Output:</label>
        </p>
        <p>
            <textarea disabled id="output" rows="10" cols="40" value={output.response}></textarea>
        </p>
        <p>
            <label htmlFor="errors">Errors:</label>
        </p>
        <p>
            <textarea disabled id="errors" rows="10" cols="40" value={output.errors}></textarea>
        </p>
    </div>
  );
}

export default App;
