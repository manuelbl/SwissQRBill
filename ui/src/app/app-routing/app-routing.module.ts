import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BillData } from '../billdata/billdata.component';
import { AboutComponent } from '../about/about.component';

const routes: Routes = [
  { path: '', redirectTo: '/bill', pathMatch: 'full' },
  { path: 'bill', component: BillData },
  { path: 'about', component: AboutComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule { }
