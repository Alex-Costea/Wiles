import Image from "next/image";

function Background()
{
    return <div className="background">
        <Image src="images/background.png"
               alt="background" fill={true} priority style={{objectFit: "cover"}}></Image>
    </div>
}

export default Background