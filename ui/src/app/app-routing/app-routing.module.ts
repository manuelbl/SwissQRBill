//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BillDataComponent } from '../billdata/billdata.component';
import { AboutComponent } from '../about/about.component';
import { SettingsComponent } from '../settings/settings.component';
import { ExamplesComponent } from '../examples/examples.component';

const routes: Routes = [
  { path: '', redirectTo: '/bill', pathMatch: 'full' },
  { path: 'bill', component: BillDataComponent },
  { path: 'about', component: AboutComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'examples', component: ExamplesComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
