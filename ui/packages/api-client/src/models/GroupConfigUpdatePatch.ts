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
import type { GroupStrategyConfig } from './GroupStrategyConfig';
import {
    GroupStrategyConfigFromJSON,
    GroupStrategyConfigFromJSONTyped,
    GroupStrategyConfigToJSON,
    GroupStrategyConfigToJSONTyped,
} from './GroupStrategyConfig';

/**
 * 
 * @export
 * @interface GroupConfigUpdatePatch
 */
export interface GroupConfigUpdatePatch {
    /**
     * 
     * @type {GroupStrategyConfig}
     * @memberof GroupConfigUpdatePatch
     */
    groupStrategyConfig?: GroupStrategyConfig | null;
}

/**
 * Check if a given object implements the GroupConfigUpdatePatch interface.
 */
export function instanceOfGroupConfigUpdatePatch(value: object): value is GroupConfigUpdatePatch {
    return true;
}

export function GroupConfigUpdatePatchFromJSON(json: any): GroupConfigUpdatePatch {
    return GroupConfigUpdatePatchFromJSONTyped(json, false);
}

export function GroupConfigUpdatePatchFromJSONTyped(json: any, ignoreDiscriminator: boolean): GroupConfigUpdatePatch {
    if (json == null) {
        return json;
    }
    return {
        
        'groupStrategyConfig': json['groupStrategyConfig'] == null ? undefined : GroupStrategyConfigFromJSON(json['groupStrategyConfig']),
    };
}

  export function GroupConfigUpdatePatchToJSON(json: any): GroupConfigUpdatePatch {
      return GroupConfigUpdatePatchToJSONTyped(json, false);
  }

  export function GroupConfigUpdatePatchToJSONTyped(value?: GroupConfigUpdatePatch | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'groupStrategyConfig': GroupStrategyConfigToJSON(value['groupStrategyConfig']),
    };
}

