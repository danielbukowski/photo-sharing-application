export interface Page<T> {
  numberOfElements: number;
  currentPage: number;
  data: Array<T>;
  totalPages: number;
  isLast: boolean;
}
