export interface Account {
  accountId: string;
  nickname: string;
  email: string;
  biography?: string;
  isEmailVerified: boolean;
  accountVerifiedAt?: string;
  roles: string[];
  permissions: string[];
}
