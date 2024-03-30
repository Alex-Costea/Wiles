import Image from "next/image";

function Background()
{
    return <div className="background">
        <Image src="https://raw.githubusercontent.com/Alex-Costea/Wiles/2f2aca84996fd8b3d77bc337d4576dcf306b1891/wiles-web-frontend/public/images/background.png"
               alt="background" fill={true} priority style={{objectFit: "cover"}}></Image>
    </div>
}

export default Background