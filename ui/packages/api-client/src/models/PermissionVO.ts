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
 * @interface PermissionVO
 */
export interface PermissionVO {
    /**
     * 
     * @type {string}
     * @memberof PermissionVO
     */
    description?: string | null;
    /**
     * 
     * @type {string}
     * @memberof PermissionVO
     */
    name: string;
}

/**
 * Check if a given object implements the PermissionVO interface.
 */
export function instanceOfPermissionVO(value: object): value is PermissionVO {
    if (!('name' in value) || value['name'] === undefined) return false;
    return true;
}

export function PermissionVOFromJSON(json: any): PermissionVO {
    return PermissionVOFromJSONTyped(json, false);
}

export function PermissionVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): PermissionVO {
    if (json == null) {
        return json;
    }
    return {
        
        'description': json['description'] == null ? undefined : json['description'],
        'name': json['name'],
    };
}

  export function PermissionVOToJSON(json: any): PermissionVO {
      return PermissionVOToJSONTyped(json, false);
  }

  export function PermissionVOToJSONTyped(value?: PermissionVO | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'description': value['description'],
        'name': value['name'],
    };
}
