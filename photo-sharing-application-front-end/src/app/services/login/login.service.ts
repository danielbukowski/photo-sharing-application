import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Credentials } from '../../models/credentials';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { Buffer } from 'buffer';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private http: HttpClient) {}

  
}
