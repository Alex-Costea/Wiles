import React from 'react';
import App from '../components/app';
import Background from "@/components/background";
import Header from "@/components/header";

export default function Home(){
    return (<React.StrictMode>
        <div id={"document"}>
            <Background/>
            <Header/>
            <App/>
        </div>
    </React.StrictMode>)
}