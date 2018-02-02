import { AppPage } from './app.po';
import { protractor } from 'protractor/built/ptor';
import { browser, by, element } from 'protractor';

describe('QR Bill App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
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
