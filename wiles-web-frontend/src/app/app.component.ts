import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'wiles-web-frontend';


  onSubmit()
  {
    window.alert(JSON.stringify(this.myForm.value))
  }

  myForm = this.formBuilder.group({
    code: 'writeline("Hello, Wiles!")',
    input: ''
  });

  constructor(private formBuilder : FormBuilder)
  {
    
  }
}
