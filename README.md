# Group Project - *Deals*

**Deals** is an Android app that allows a user to explore and enter/share deals (for example, "Buy one small pizza, get a second one free", or "All appetizers half price during Happy Hour") near their current location, both crowdsourced deals and deals entered by participating businesses.

## Core Flows User Stories

* [x] User can **register as a new user**
* [x] User can **sign into their existing account**
* [x] User **remains logged in after closing and reopening the app**
* [x] User also has the **option to log in with Facebook**
* [x] User can **logout**
* [x] User can **see all deals near them displayed by location on a map**
* [x] User can **tap a deal on the map to navigate from their current location to the deal location**
* [x] **Deals are also displayed in a list format**
* [x] User sees **progress indicator while deals are being loaded**
* [x] User can **tap a Deal in the list to see more details or hide details**
* [x] User can **create deals**
  * [x] Deal **locations are set with autocomplete via the Google Places API**
  * [x] Deal details can **include an image from the android camera or photo gallery**
  * [x] **Created deal appears at the top of the My Deals list**
* [x] User can **manage self-created deals**
  * [x] User can **see a list of all of their created deals**
  * [x] User can **edit deals they created**
  * [x] User can **delete deals they created**
* [x] User can **like/favorite a deal**
* [x] User can **see a list of all of their liked/favorited deals**
* [x] App **backend using Parse hosted on Heroku**

## Project Requirements from http://courses.codepath.com/courses/intro_to_android/pages/group_project and elsewhere

* [x] Must have at least three separate "activities" or screens supporting user interaction
* [x] Must be data-driven with dynamic information or media being displayed
* [x] Must use a RESTful API to source the data that is populated into the application
* [x] Must use local persistence either through files, preferences or SQLite
* [x] Make sure to leverage at least two mobile-oriented features

## Optional User Stories

* [x] Floating notifications widget (like Facebook Messenger) 
* [ ] User can configure notifications/alerts
* [x] Background service notifies user of deals as they are added 
* [ ] Background service notifies user as location changes bring user into proximity with different deals
* [ ] User can see a list of all their notifications/alerts
* [ ] App supports deals that can be redeemed within the app in addition to general informational deals
* [ ] User can filter deals by category

## Wireframes

Here are [some preliminary wireframes for our app](https://jktdeals.mybalsamiq.com/projects/jktdeals/grid).

## Video Walkthrough 

See our video walkthroughs of some implemented user stories at https://github.com/jktdeals/Deals/issues/35

## Notes

Some UI issues/behaviors need some cleaning up, but basically we have amazing functionality.

## Open-source libraries used

- [Parse](https://github.com/ParsePlatform/parse-server) - Mobile Backend as a Service

## License

    Copyright 2016 Jose Villanueva, Kartik Kulkarni, and Tané Tachyon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
