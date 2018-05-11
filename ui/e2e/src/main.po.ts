import { browser, by, element } from 'protractor';

export class MainPage {
  navigateTo() {
    return browser.get('/');
  }

  getNavbarText() {
    return element(by.css('qrbill-root .navbar-button')).getText();
  }

  getPreviewButton() {
    return element(by.css('.sticky-footer button'));
  }
}
