import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'wiles-web-frontend';
  CSFRLoaded = false

  loadCSFR()
  {
    if(!this.CSFRLoaded)
    {
      const headers = new HttpHeaders({
        'Cache-Control':  'no-cache, no-store, must-revalidate, post- check=0, pre-check=0',
        'Pragma': 'no-cache',
        'Expires': '0'
      });
      this.http.get<any>("getcsfr", { withCredentials: true, headers : headers}).subscribe(
        () => {console.log("CSFR request success")},
        () => {console.log("CSFR request error")},
        () => {console.log("CSFR request complete")}
      )
      this.CSFRLoaded=true
    }
  }


  onSubmit()
  {
    this.loadCSFR()
    this.http.put<any>("/run",this.myForm.value, { withCredentials: true }).subscribe(data =>
      {
        let response : string = data.response;
        let errors : string | null = data.errors;
        (<HTMLInputElement>document.getElementById("output")).value = response;
        (<HTMLInputElement>document.getElementById("errors")).value = errors?.slice(5,-4) ?? "";
      })

  }

  myForm = this.formBuilder.group({
    code:
`let name := read_line()
writeline("Hello, " + name + "!")`,
    input: 'Wiles'
  });

  constructor(private formBuilder : FormBuilder,
    private http: HttpClient,
    )
  {

  }
}
