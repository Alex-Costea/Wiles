import React from 'react'
import ReactDOM from 'react-dom/client'
import App from '../app/app.tsx';
import Header from "../header/header.tsx";
import './main.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <div id={"document"}>
            <Header/>
            <App/>
        </div>
    </React.StrictMode>
)
