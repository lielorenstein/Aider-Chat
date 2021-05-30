import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class APIService {

  ENDPOINT = 'https://vaye9ssus4.execute-api.us-east-1.amazonaws.com/dev/speak';
  constructor(private http:HttpClient) {}
  speak(data) {
    return this.http.post(this.ENDPOINT, data);
  }
}
