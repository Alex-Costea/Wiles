import Image from "next/image";

function Header()
{
    return <header className="App-header">
        <Image src="logo_pastel.svg" width={500} height={500} className="App-logo" alt="Wiles logo" priority/>
    </header>
}

export default Header