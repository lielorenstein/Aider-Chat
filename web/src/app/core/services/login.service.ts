import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { loginDetails } from '../models/login-details.model';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private userName: string;
  private isAuthicate:  BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  constructor(private router: Router) {

   }

  isLoggedIn() : Observable<boolean> {
    return this.isAuthicate.asObservable();
  }


  Login(loginDetails: loginDetails):void{
    console.log("asd");
    if(loginDetails.userName == 'a' && loginDetails.password == '123')
      this.userName = loginDetails.userName;
      this.isAuthicate.next(true);
      this.router.navigate(['']);

  }


}
