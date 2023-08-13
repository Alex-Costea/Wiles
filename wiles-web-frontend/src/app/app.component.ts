import { HttpClient } from '@angular/common/http';
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
    this.http.put<any>("http://localhost:8080/run",this.myForm.value).subscribe(data =>
      {
        //window.alert(JSON.stringify(data))
        (<HTMLInputElement>document.getElementById("output")).value = data.response;
        (<HTMLInputElement>document.getElementById("errors")).value = data.errors;
      })
    
  }

  myForm = this.formBuilder.group({
    code: 'writeline("Hello, Wiles!")',
    input: ''
  });

  constructor(private formBuilder : FormBuilder,
    private http: HttpClient,
    )
  {
    
  }
}
