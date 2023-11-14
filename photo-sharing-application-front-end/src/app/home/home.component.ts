import { Component, OnInit } from '@angular/core';
import { ImageService } from '../image/image.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  listOfImagesAsIds!: Array<string>;

  constructor(private imageService: ImageService) { }

  ngOnInit(): void {
    this.imageService.getLatestImagesInFormOfIdList()
    .subscribe({
      next: d => this.listOfImagesAsIds = d.data
    });
  }

}
