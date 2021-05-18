import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { loginDetails } from 'src/app/core/models/login-details.model';
import { LoginService } from 'src/app/core/services/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  userName: string;
  password: string;
  loginForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private loginService: LoginService) { }

  ngOnInit(): void {

    this.loginForm = this.formBuilder.group({
      userName: [this.userName, [Validators.required]],
      password: [this.password, [Validators.required]]
    });
  }


  onLogin(){
    console.log("hey there");
    if(this.loginForm.valid){
      let loginDetails: loginDetails = this.loginForm.value;
        this.loginService.Login(loginDetails);
    }
  }

}
