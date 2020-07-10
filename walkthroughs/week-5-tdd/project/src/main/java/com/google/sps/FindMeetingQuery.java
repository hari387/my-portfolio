// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> acceptableTimes = modifiableQuery(events, request, true);
    if(request.getAttendees().size() > 0 && acceptableTimes.size() == 0){
      acceptableTimes = modifiableQuery(events, request, false);
    }
    return acceptableTimes;
  }

  private Collection<TimeRange> modifiableQuery(Collection<Event> events, MeetingRequest request, boolean optional) {

    Collection<String> eventAttendees;
    Collection<String> meetingAttendees = new ArrayList<String>(request.getAttendees());
    
    if(optional){
      meetingAttendees.addAll(request.getOptionalAttendees());
    }

    if(request.getDuration() >= 24*60){
      return new ArrayList();
    }

    if(meetingAttendees.size() == 0){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    // Events containing attendees in the meeting request.
    List<TimeRange> relevantEventTimes = new ArrayList<TimeRange>();

    for(Event event : events){
      if(event.getWhen().duration() == 0) continue;
      eventAttendees = event.getAttendees();
      for(String attendee : eventAttendees){
        if(meetingAttendees.contains(attendee)){
          relevantEventTimes.add(event.getWhen());
          break;
        }
      }
    }

    relevantEventTimes.sort(TimeRange.ORDER_BY_START);

    List<TimeRange> nonOverlappingTimes = new ArrayList<TimeRange>();

    if(relevantEventTimes.size() == 0){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    nonOverlappingTimes.add(relevantEventTimes.get(0));

    TimeRange squashedTime;

    for(int i = 1;i<relevantEventTimes.size();i++){
      squashedTime = nonOverlappingTimes.get(nonOverlappingTimes.size() - 1);
      if(relevantEventTimes.get(i).overlaps(squashedTime)){
        nonOverlappingTimes.set(
          nonOverlappingTimes.size() - 1,
          TimeRange.fromStartEnd(
            squashedTime.start(),
            relevantEventTimes.get(i).end() > squashedTime.end() ? relevantEventTimes.get(i).end() : squashedTime.end(),
            false
          )
        );
      } else {
        nonOverlappingTimes.add(relevantEventTimes.get(i));
      }
    }

    List<TimeRange> acceptableTimes = new ArrayList<TimeRange>();

    addIfAcceptable(
      acceptableTimes,
      TimeRange.fromStartEnd(
        0,
        nonOverlappingTimes.get(0).start(),
        false
      ),
      request.getDuration()
    );

    for(int i = 0;i<nonOverlappingTimes.size() - 1;i++){
      addIfAcceptable(
        acceptableTimes,
        TimeRange.fromStartEnd(
          nonOverlappingTimes.get(i).end(),
          nonOverlappingTimes.get(i+1).start(),
          false
        ),
        request.getDuration()
      );
    }

    addIfAcceptable(
      acceptableTimes,
      TimeRange.fromStartEnd(
        nonOverlappingTimes.get(nonOverlappingTimes.size() - 1).end(),
        24*60,
        false
      ),
      request.getDuration()
    );

    return acceptableTimes;
  }

  public void addIfAcceptable(List<TimeRange> timeRanges, TimeRange timeRange, long duration){
    if(timeRange.duration() >= duration){
      timeRanges.add(timeRange);
    }
  }

  public void printTimeRanges(Collection<TimeRange> timeRanges){
    for(TimeRange t : timeRanges){
      int startHr = t.start() / 60;
      int startMin = t.start() % 60;
      int endHr = t.end() / 60;
      int endMin = t.end() % 60;
      
      System.out.printf("%d:%d to %d:%d\n",startHr,startMin,endHr,endMin);
    }
  }

}
