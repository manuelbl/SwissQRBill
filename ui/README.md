# QR Bill UI

This project currently uses Angular 6.0.1 and Angular Material 6.0.1.

## Development server

Run `npm start` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

The app expects the QR Bill service to be running on http://localhost:8081 (also see `proxy-dev.json`). 

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `npm run build` to build the project. The build artifacts will be stored in the `dist/` directory. The command uses the `--prod` flag for a production build.

The gradle script uses the same command. It can be run with `gradle build` or `gradle :ui:build` (from the parent directory). 

## Running unit tests

Run `npm test` to execute the unit tests via [Karma](https://karma-runner.github.io). The QR Bill service does not need to be running. Some 404 errors will appear in the output. They are caused by SVG images that cannot be loaded; they can be ignored as they do not affect the tests.

## Running end-to-end tests

To exeute the end-to-end tests via [Protractor](http://www.protractortest.org/), first start the QR Bill service, then start a dev server (`npm start`) and finally execute `npm run e2e`.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
