/* tslint:disable */
/* eslint-disable */
/**
 * HoshizoraPics API
 * API for testing and demonstration purposes.
 *
 * The version of the OpenAPI document: latest
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


/**
 * 
 * @export
 */
export const JavaTimeMonth = {
    January: 'JANUARY',
    February: 'FEBRUARY',
    March: 'MARCH',
    April: 'APRIL',
    May: 'MAY',
    June: 'JUNE',
    July: 'JULY',
    August: 'AUGUST',
    September: 'SEPTEMBER',
    October: 'OCTOBER',
    November: 'NOVEMBER',
    December: 'DECEMBER'
} as const;
export type JavaTimeMonth = typeof JavaTimeMonth[keyof typeof JavaTimeMonth];


export function instanceOfJavaTimeMonth(value: any): boolean {
    for (const key in JavaTimeMonth) {
        if (Object.prototype.hasOwnProperty.call(JavaTimeMonth, key)) {
            if (JavaTimeMonth[key as keyof typeof JavaTimeMonth] === value) {
                return true;
            }
        }
    }
    return false;
}

export function JavaTimeMonthFromJSON(json: any): JavaTimeMonth {
    return JavaTimeMonthFromJSONTyped(json, false);
}

export function JavaTimeMonthFromJSONTyped(json: any, ignoreDiscriminator: boolean): JavaTimeMonth {
    return json as JavaTimeMonth;
}

export function JavaTimeMonthToJSON(value?: JavaTimeMonth | null): any {
    return value as any;
}
