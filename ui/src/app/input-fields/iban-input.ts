/**
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 *
 * Partially according to "Creating a custom form field control" guide
 * (https://material.angular.io/guide/creating-a-custom-form-field-control).
 * Partially a copy of MatInput.
 * Copyright Google LLC All Rights Reserved.
 */

import { OnChanges, OnDestroy, OnInit, DoCheck, Input, ElementRef, Self, Optional, Inject, Directive } from "@angular/core";
import { NgControl, NgForm, FormGroupDirective, ControlValueAccessor } from "@angular/forms";
import { MatFormFieldControl, _MatInputMixinBase, CanUpdateErrorState, ErrorStateMatcher } from "@angular/material";
import { coerceBooleanProperty } from "@angular/cdk/coercion";
import { Platform } from "@angular/cdk/platform";
import { Subject } from "rxjs/Subject";

let nextUniqueId = 0;

/** Directive that allows a native input to work inside a `MatFormField`. */
@Directive({
  selector: `input[ibanInput]`,
  exportAs: 'ibanInput',
  host: {
    'class': 'mat-input-element mat-form-field-autofill-control',
    '[class.mat-input-server]': '_isServer',
    '[attr.id]': 'id',
    '[attr.placeholder]': 'placeholder',
    '[disabled]': 'disabled',
    '[required]': 'required',
    '[readonly]': 'readonly',
    '[attr.aria-describedby]': '_ariaDescribedby || null',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-required]': 'required.toString()',
    '(blur)': '_focusChanged(false)',
    '(focus)': '_focusChanged(true)',
    '(input)': '_onInput()'
  },
  providers: [
    { provide: MatFormFieldControl, useExisting: IBANInputDirective }
  ],
})
export class IBANInputDirective extends _MatInputMixinBase implements MatFormFieldControl<string>, OnChanges,
    OnDestroy, DoCheck, CanUpdateErrorState, ControlValueAccessor {
  protected _uid = `iban-input-${nextUniqueId++}`;
  protected _rawValue: any;
  protected _previousNativeValue: any;

  /** The aria-describedby attribute on the input for improved a11y. */
  _ariaDescribedby: string;

  /** Whether the component is being rendered on the server. */
  _isServer = false;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  focused: boolean = false;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  readonly stateChanges: Subject<void> = new Subject<void>();

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  controlType: string = 'iban-input';

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  autofilled = false;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  @Input()
  get disabled(): boolean {
    if (this.ngControl && this.ngControl.disabled !== null) {
      return this.ngControl.disabled;
    }
    return this._disabled;
  }
  set disabled(value: boolean) {
    this._disabled = coerceBooleanProperty(value);

    // Browsers may not fire the blur event if the input is disabled too quickly.
    // Reset from here to ensure that the element doesn't become stuck.
    if (this.focused) {
      this.focused = false;
      this.stateChanges.next();
    }
  }
  protected _disabled = false;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) { this._id = value || this._uid; }
  protected _id: string;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  @Input() placeholder: string = '';

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  @Input()
  get required(): boolean { return this._required; }
  set required(value: boolean) { this._required = coerceBooleanProperty(value); }
  protected _required = false;

  /** An object used to control when error messages are shown. */
  @Input() errorStateMatcher: ErrorStateMatcher;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  @Input()
  get value(): string { return this._rawValue; }
  set value(value: string) {
    let raw = this.rawValue(value);
    if (raw !== this._rawValue) {
      this._rawValue = raw;
      this._elementRef.nativeElement.value = this.formattedValue(raw);
      this.stateChanges.next();
      if (this._onChange)
        this._onChange(raw);
    }
  }

  /** Whether the element is readonly. */
  @Input()
  get readonly(): boolean { return this._readonly; }
  set readonly(value: boolean) { this._readonly = coerceBooleanProperty(value); }
  private _readonly = false;

  constructor(protected _elementRef: ElementRef,
              protected _platform: Platform,
              /** @docs-private */
              @Optional() @Self() public ngControl: NgControl,
              @Optional() _parentForm: NgForm,
              @Optional() _parentFormGroup: FormGroupDirective,
              _defaultErrorStateMatcher: ErrorStateMatcher) {
    super(_defaultErrorStateMatcher, _parentForm, _parentFormGroup, ngControl);

    this._previousNativeValue = this._elementRef.nativeElement.value;
    this._rawValue = this.rawValue(this._previousNativeValue);

    // Force setter to be called in case id was not specified.
    this.id = this.id;

    if (this.ngControl != null) { this.ngControl.valueAccessor = this; }

    // On some versions of iOS the caret gets stuck in the wrong place when holding down the delete
    // key. In order to get around this we need to "jiggle" the caret loose. Since this bug only
    // exists on iOS, we only bother to install the listener on iOS.
    if (_platform.IOS) {
      _elementRef.nativeElement.addEventListener('keyup', (event: Event) => {
        let el = event.target as HTMLInputElement;
        if (!el.value && !el.selectionStart && !el.selectionEnd) {
          // Note: Just setting `0, 0` doesn't fix the issue. Setting
          // `1, 1` fixes it for the first time that you type text and
          // then hold delete. Toggling to `1, 1` and then back to
          // `0, 0` seems to completely fix it.
          el.setSelectionRange(1, 1);
          el.setSelectionRange(0, 0);
        }
      });
    }

    this._isServer = !this._platform.isBrowser;
  }

  ngOnChanges() {
    this.stateChanges.next();
  }

  ngOnDestroy() {
    this.stateChanges.complete();
  }

  ngDoCheck() {
    if (this.ngControl) {
      // We need to re-evaluate this on every change detection cycle, because there are some
      // error triggers that we can't subscribe to (e.g. parent form submissions). This means
      // that whatever logic is in here has to be super lean or we risk destroying the performance.
      this.updateErrorState();
    }

    // We need to dirty-check the native element's value, because there are some cases where
    // we won't be notified when it changes (e.g. the consumer isn't using forms or they're
    // updating the value using `emitEvent: false`).
    this._dirtyCheckNativeValue();
  }

  /** Focuses the input. */
  focus(): void { this._elementRef.nativeElement.focus(); }

  /** Callback for the cases where the focused state of the input changes. */
  _focusChanged(isFocused: boolean) {
    if (isFocused !== this.focused && !this.readonly) {
      this.focused = isFocused;
      this.stateChanges.next();
      if (!isFocused) {
        this._previousNativeValue = this.formattedValue(this._rawValue);
        this._elementRef.nativeElement.value = this._previousNativeValue;
        if (this._onTouched)
          this._onTouched();
      }
    }
  }

  _onInput() {
    // This is a noop function and is used to let Angular know whenever the value changes.
    // Angular will run a new change detection each time the `input` event has been dispatched.
    // It's necessary that Angular recognizes the value change, because when floatingLabel
    // is set to false and Angular forms aren't used, the placeholder won't recognize the
    // value changes and will not disappear.
    // Listening to the input event wouldn't be necessary when the input is using the
    // FormsModule or ReactiveFormsModule, because Angular forms also listens to input events.
  }

  /** Does some manual dirty checking on the native input `value` property. */
  protected _dirtyCheckNativeValue() {
    const newValue = this._elementRef.nativeElement.value;

    if (this._previousNativeValue !== newValue) {
      this._previousNativeValue = newValue;
      this._rawValue = this.rawValue(newValue);
      this.stateChanges.next();
      if (this._onChange)
        this._onChange(this._rawValue);
    }
  }

  /** Checks whether the input is invalid based on the native validation. */
  protected _isBadInput() {
    // The `validity` property won't be present on platform-server.
    let validity = (this._elementRef.nativeElement as HTMLInputElement).validity;
    return validity && validity.badInput;
  }

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  get empty(): boolean {
    return !this._elementRef.nativeElement.value && !this._isBadInput();
  }

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  get shouldLabelFloat(): boolean { return this.focused || !this.empty; }

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  setDescribedByIds(ids: string[]) { this._ariaDescribedby = ids.join(' '); }

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  onContainerClick() { this.focus(); }

  writeValue(obj: any): void {
    this.value = obj;
  }

  private _onChange = (_: any) => { };
  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  private _onTouched = () => { };
  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  private rawValue(val: string): string {
    if (!val)
      return null;
    return val.replace(/\s/g, "");
  }

  private formattedValue(val: string): string {
    if (!val)
      return null;

    let formatted = "";
    let len = val.length;
    for (let p = 0; p < len; p += 4) {
      let e = p + 4 <= len ? p + 4 : len;
      formatted += val.substring(p, p + 4);
      if (e < len)
        formatted += " ";
    }
    return formatted;
  }
}