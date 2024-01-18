import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Page } from 'src/app/models/page';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
})
export class PaginationComponent {
  @Output() pageNumber = new EventEmitter<number>();
  @Input() page!: Page<unknown>;

  fetchNextPage(): void {
    if (this.isPageLast()) return;
    this.pageNumber.emit(this.page.currentPage + 1);
  }

  fetchPreviousPage(): void {
    if (this.isPageFirst()) return;
    this.pageNumber.emit(this.page.currentPage - 1);
  }

  isPageFirst(): boolean {
    return this.page.currentPage <= 0;
  }
  isPageLast(): boolean {
    return this.page.isLast;
  }
}
