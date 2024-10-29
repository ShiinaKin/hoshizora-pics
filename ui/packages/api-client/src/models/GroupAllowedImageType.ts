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

import { mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface GroupAllowedImageType
 */
export interface GroupAllowedImageType {
    /**
     * 
     * @type {Set<string>}
     * @memberof GroupAllowedImageType
     */
    allowedImageTypes: Set<string>;
}

/**
 * Check if a given object implements the GroupAllowedImageType interface.
 */
export function instanceOfGroupAllowedImageType(value: object): value is GroupAllowedImageType {
    if (!('allowedImageTypes' in value) || value['allowedImageTypes'] === undefined) return false;
    return true;
}

export function GroupAllowedImageTypeFromJSON(json: any): GroupAllowedImageType {
    return GroupAllowedImageTypeFromJSONTyped(json, false);
}

export function GroupAllowedImageTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): GroupAllowedImageType {
    if (json == null) {
        return json;
    }
    return {
        
        'allowedImageTypes': new Set(json['allowedImageTypes']),
    };
}

  export function GroupAllowedImageTypeToJSON(json: any): GroupAllowedImageType {
      return GroupAllowedImageTypeToJSONTyped(json, false);
  }

  export function GroupAllowedImageTypeToJSONTyped(value?: GroupAllowedImageType | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'allowedImageTypes': Array.from(value['allowedImageTypes'] as Set<any>),
    };
}

