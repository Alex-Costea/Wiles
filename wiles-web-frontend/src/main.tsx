import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './app';
import Header from "./header";
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <div id={"document"}>
            <Header/>
            <App/>
        </div>
    </React.StrictMode>
)
