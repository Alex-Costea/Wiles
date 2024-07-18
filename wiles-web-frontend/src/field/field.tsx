import React from "react";
import ContentEditable, {ContentEditableEvent} from "react-contenteditable";
import './field.css';

interface FieldProps {
    id : string,
    label?: string
    innerHTML : string,
    onChange? :  (event: ContentEditableEvent) => void,
    disabled? : boolean}

function Field({id, label = "", innerHTML, onChange = () => {}, disabled = false} : FieldProps)
{
    function onPaste(e : React.ClipboardEvent) {
        e.preventDefault()
        const text = e.clipboardData?.getData("text/plain")
        const selection = window.getSelection();
        if (!selection?.rangeCount) return;
        selection.deleteFromDocument();
        selection.getRangeAt(0).insertNode(document.createTextNode(text));
        selection.collapseToEnd();
    }

    return <React.Fragment>
        <p>
            <label htmlFor={id}>{label}</label>
        </p>
        <p className={"fieldOuter"}>
            <ContentEditable id={id} className={"field"} tagName={"span"} spellCheck={false} onPaste={onPaste}
                             onChange={onChange} html={innerHTML} disabled={disabled}/>
        </p>
    </React.Fragment>


}

export default Field