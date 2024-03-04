import React from "react";
import ContentEditable, {ContentEditableEvent} from "react-contenteditable";

interface FieldProps {
    id : string,
    label?: string
    innerHTML : string,
    onChange? :  (event: ContentEditableEvent) => void,
    disabled? : boolean}

function Field({id, label = "", innerHTML, onChange = () => {}, disabled = false} : FieldProps)
{
    return <React.Fragment>
        <p>
            <label htmlFor={id}>{label}</label>
        </p>
        <p>
            <ContentEditable id={id} className={"field"} tagName={"span"} spellCheck={false}
                             onChange={onChange} html={innerHTML} disabled={disabled}/>
        </p>
    </React.Fragment>


}

export default Field