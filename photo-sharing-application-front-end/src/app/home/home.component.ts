import { Component, OnInit } from '@angular/core';
import { ImageService } from '../image/image.service';
import { Observable } from 'rxjs';
import { Page } from '../model/page';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  imagePage$!: Observable<Page<String>>;

  constructor(private imageService: ImageService) { }

  ngOnInit(): void {
    this.imagePage$ = this.imageService.getPageOfLatestImages(0);
  }

}
