import { browser, by, element } from 'protractor';
import { promise } from 'selenium-webdriver';
import { ElementFinder } from 'protractor/built/element';

export class ExamplesPage {
    navigateTo(): promise.Promise<any> {
        return browser.get('/examples');
    }

    getAppExample(index: number): ElementFinder {
        return element(by.css('.examples mat-card:nth-child(' + index + ')'));
    }

    getNumAppExamples(): promise.Promise<number> {
        return element.all(by.css('.examples mat-card')).count();
    }

    getUseButton(index: number): ElementFinder {
        return element(by.css('.examples mat-card:nth-child(' + index + ') button'));
    }
}
