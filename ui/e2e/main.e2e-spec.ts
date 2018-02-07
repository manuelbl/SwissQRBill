import { MainPage } from './main.po';
import { protractor } from 'protractor/built/ptor';
import { browser, by, element } from 'protractor';

describe('Main Page', () => {
  let page: MainPage;

  beforeEach(() => {
    page = new MainPage();
  });

  it('should display UI', () => {
    page.navigateTo();
    expect(page.getNavbarText()).toEqual('Swiss QR Bill');
  });

  it('should open preview dialog', () => {
    let EC = protractor.ExpectedConditions;

    page.navigateTo();
    page.getPreviewButton().click();

    let condition = EC.visibilityOf(element(by.css('.preview-section')));
    browser.wait(condition, 1000);
  });
});
