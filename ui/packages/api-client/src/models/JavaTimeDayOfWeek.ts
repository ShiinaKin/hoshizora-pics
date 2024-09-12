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
export const JavaTimeDayOfWeek = {
    Monday: 'MONDAY',
    Tuesday: 'TUESDAY',
    Wednesday: 'WEDNESDAY',
    Thursday: 'THURSDAY',
    Friday: 'FRIDAY',
    Saturday: 'SATURDAY',
    Sunday: 'SUNDAY'
} as const;
export type JavaTimeDayOfWeek = typeof JavaTimeDayOfWeek[keyof typeof JavaTimeDayOfWeek];


export function instanceOfJavaTimeDayOfWeek(value: any): boolean {
    for (const key in JavaTimeDayOfWeek) {
        if (Object.prototype.hasOwnProperty.call(JavaTimeDayOfWeek, key)) {
            if (JavaTimeDayOfWeek[key as keyof typeof JavaTimeDayOfWeek] === value) {
                return true;
            }
        }
    }
    return false;
}

export function JavaTimeDayOfWeekFromJSON(json: any): JavaTimeDayOfWeek {
    return JavaTimeDayOfWeekFromJSONTyped(json, false);
}

export function JavaTimeDayOfWeekFromJSONTyped(json: any, ignoreDiscriminator: boolean): JavaTimeDayOfWeek {
    return json as JavaTimeDayOfWeek;
}

export function JavaTimeDayOfWeekToJSON(value?: JavaTimeDayOfWeek | null): any {
    return value as any;
}
