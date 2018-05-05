import { ExamplesPage } from './examples.po';
import { protractor } from 'protractor/built/ptor';
import { browser, by, element } from 'protractor';
import { MainPage } from './main.po';

describe('Examples Page', () => {
  let page: ExamplesPage;

  beforeEach(() => {
    page = new ExamplesPage();
  });

  it('should display examples', () => {
    page.navigateTo();
    expect(page.getNumAppExamples()).toBeGreaterThan(3);
    expect(page.getAppExample(1)).toBeTruthy();
    expect(page.getAppExample(2)).toBeTruthy();
    expect(page.getAppExample(3)).toBeTruthy();
    expect(page.getAppExample(4)).toBeTruthy();
  });

  it('should display images', () => {
    const EC = protractor.ExpectedConditions;
    for (let i = 1; i <= 4; i++) {
        browser.wait(
            () => browser.executeScript(
                'return arguments[0].complete && arguments[0].naturalWidth > 0;',
                page.getAppExample(i).element(by.css('img')).getWebElement()),
            3000
        );
      }
  });

  it('should navigate to main page', () => {
    page.getUseButton(2).click();
    browser.waitForAngular();
    const mainPage = new MainPage();
    expect(mainPage.getPreviewButton()).toBeTruthy();
  });
});
