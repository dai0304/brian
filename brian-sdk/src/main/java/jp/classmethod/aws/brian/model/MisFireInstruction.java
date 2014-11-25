/*
 * Copyright 2011 Daisuke Miyamoto. (http://d.hatena.ne.jp/daisuke-m)
 * Created on 2014/11/20
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.classmethod.aws.brian.model;

import java.util.Arrays;
import java.util.Calendar;

/**
 * TODO for daisuke
 */
public enum MisFireInstruction {
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the <code>updateAfterMisfire()</code> method will be called
	 * on the {@link BrianTrigger} to determine the mis-fire instruction,
	 * which logic will be trigger-implementation-dependent.
	 * 
	 * <p>
	 * In order to see if this instruction fits your needs, you should look at
	 * the documentation for the <code>getSmartMisfirePolicy()</code> method
	 * on the particular {@link BrianTrigger} implementation you are using.
	 * </p>
	 */
	SMART_POLICY(0, BrianTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that the
	 * {@link BrianTrigger} will never be evaluated for a misfire situation,
	 * and that the scheduler will simply try to fire it as soon as it can,
	 * and then update the Trigger as if it had fired at the proper time.
	 * 
	 * <p>NOTE: if a trigger uses this instruction, and it has missed
	 * several of its scheduled firings, then several rapid firings may occur
	 * as the trigger attempt to catch back up to where it would have been.
	 * For example, a SimpleTrigger that fires every 15 seconds which has
	 * misfired for 5 minutes will fire 20 times once it gets the chance to
	 * fire.</p>
	 */
	IGNORE_MISFIRE_POLICY(-1, BrianTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianCronTrigger} wants to be fired now
	 * by Brian scheduler.
	 */
	FIRE_ONCE_NOW(1, BrianCronTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianCronTrigger} wants to have it's
	 * next-fire-time updated to the next time in the schedule after the
	 * current time (taking into account any associated {@link Calendar},
	 * but it does not want to be fired now.
	 */
	DO_NOTHING(2, BrianCronTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianSimpleTrigger} wants to be fired
	 * now by Brian scheduler.
	 * 
	 * <p>
	 * <i>NOTE:</i> This instruction should typically only be used for
	 * 'one-shot' (non-repeating) Triggers. If it is used on a trigger with a
	 * repeat count &gt; 0 then it is equivalent to the instruction
	 * {@link #RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT}.
	 * </p>
	 */
	FIRE_NOW(1, BrianSimpleTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianSimpleTrigger} wants to be
	 * re-scheduled to 'now' (even if the associated {@link Calendar}
	 * excludes 'now') with the repeat count left as-is.  This does obey the
	 * {@link BrianTrigger} end-time however, so if 'now' is after the
	 * end-time the {@link BrianTrigger} will not fire again.
	 * 
	 * <p>
	 * <i>NOTE:</i> Use of this instruction causes the trigger to 'forget'
	 * the start-time and repeat-count that it was originally setup with (this
	 * is only an issue if you for some reason wanted to be able to tell what
	 * the original values were at some later time).
	 * </p>
	 */
	RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT(2, BrianSimpleTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianSimpleTrigger} wants to be
	 * re-scheduled to 'now' (even if the associated {@link Calendar}
	 * excludes 'now') with the repeat count set to what it would be, if it had
	 * not missed any firings.  This does obey the {@link BrianTrigger} end-time
	 * however, so if 'now' is after the end-time the {@link BrianTrigger} will
	 * not fire again.
	 * 
	 * <p>
	 * <i>NOTE:</i> Use of this instruction causes the trigger to 'forget'
	 * the start-time and repeat-count that it was originally setup with.
	 * Instead, the repeat count on the trigger will be changed to whatever
	 * the remaining repeat count is (this is only an issue if you for some
	 * reason wanted to be able to tell what the original values were at some
	 * later time).
	 * </p>
	 * 
	 * <p>
	 * <i>NOTE:</i> This instruction could cause the {@link BrianTrigger}
	 * to go to the 'COMPLETE' state after firing 'now', if all the
	 * repeat-fire-times where missed.
	 * </p>
	 */
	RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT(3, BrianSimpleTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianSimpleTrigger} wants to be
	 * re-scheduled to the next scheduled time after 'now' - taking into
	 * account any associated {@link Calendar}, and with the
	 * repeat count set to what it would be, if it had not missed any firings.
	 * 
	 * <p>
	 * <i>NOTE/WARNING:</i> This instruction could cause the {@link BrianTrigger}
	 * to go directly to the 'COMPLETE' state if all fire-times where missed.
	 * </p>
	 */
	RESCHEDULE_NEXT_WITH_REMAINING_COUNT(4, BrianSimpleTrigger.class),
	
	/**
	 * Instructs the Brian Scheduler that upon a mis-fire
	 * situation, the {@link BrianSimpleTrigger} wants to be
	 * re-scheduled to the next scheduled time after 'now' - taking into
	 * account any associated {@link Calendar}, and with the
	 * repeat count left unchanged.
	 * 
	 * <p>
	 * <i>NOTE/WARNING:</i> This instruction could cause the {@link BrianTrigger}
	 * to go directly to the 'COMPLETE' state if the end-time of the trigger
	 * has arrived.
	 * </p>
	 */
	RESCHEDULE_NEXT_WITH_EXISTING_COUNT(5, BrianSimpleTrigger.class);
	
	public static MisFireInstruction fromNumberAndClass(int misfireInstruction, Class<? extends BrianTrigger> clazz) {
		return Arrays.stream(values())
			.filter(ins -> ins.clazz.isAssignableFrom(clazz) && ins.value == misfireInstruction)
			.findFirst().get();
	}
	
	
	public final int value;
	
	public final Class<? extends BrianTrigger> clazz;
	
	
	MisFireInstruction(int value, Class<? extends BrianTrigger> clazz) {
		this.value = value;
		this.clazz = clazz;
	}
}
