## 🚀 Deepsky App - What is this project about?

The application is created for people who is interested in astronomy discoveries. Every day the new picture related to astronomy is popping up thanks to NASA - APOD service. In addition, every picture is presented with an explanation and title describing what is show no the user. Also, when some pictures has copyright data to it, application shows it.

"Picture of The Day" is available for 24h, but we can preserve it in the local storage by adding them to "The Favourite" list.

<b>Astronomy Picture of the Day (APOD) </b> is originated, written, coordinated, and edited since 1995 by Robert Nemiroff and Jerry Bonnell. The APOD archive contains the largest collection of annotated astronomical images on the internet. In real life, Robert and Jerry are two professional astronomers who spend most of their time researching the universe. Robert is a professor at Michigan Technological University in Houghton, Michigan, USA, while Jerry is a scientist at NASA's Goddard Space Flight Center in Greenbelt, Maryland USA.

## How the design looks like?

Picture of the day screen:

<img src="https://user-images.githubusercontent.com/45050205/157853432-719c282b-2e7d-47e3-9d29-f6aefa767876.jpg" width="280">&nbsp;
<img src="https://user-images.githubusercontent.com/45050205/157853439-281bf1d0-0aa5-402c-90a7-b31a18669c6e.jpg" width="280">&nbsp; &nbsp;<br>

My Favourite screen and one selected item:

<img src="https://user-images.githubusercontent.com/45050205/157853465-68d3fb8b-5e08-4663-9ead-60f245cd6eea.jpg" width="280">&nbsp;
<img src="https://user-images.githubusercontent.com/45050205/157853473-750ef72e-a5e3-404b-b997-71d472784652.jpg" width="280">&nbsp;
<img src="https://user-images.githubusercontent.com/45050205/157853484-b6bc93a0-a58c-404f-ba81-54436205d971.jpg" width="280">&nbsp;
<br>

## What technologies was used?

To implement that project **Android** and **Kotlin** language was used with min. SDK 24 (Android 7.0). The program uses **Navation Component** with NavGraph and BottomNavigationView. **Glide** library was used to load the photo from the APOD API and from the local datasource. To zoom-in and out the picture the project use **PhotoView** library and widget (source: https://github.com/Baseflow/PhotoView).

With the use of  **Clean Architecture** and  **MVVM** the presentation, logic and database layers are separated. The data layer has two sources - APOD API handled by **Retrofit**, and local database implemented by **Room Database**.

In this project you can also find **TypeConverter** provided by Room Database which converts Bitmap from the given API to the ByteArray type in which the pictures are saved in local database. The other direction is also done.

Other technologies that was used: **LiveData, LiveEvent, Timber, Hilt (Dependency Injection)**
<br>

## Who is the author?

👨‍💻 Implemented by: [Wojciech Kula] <br>
🌌 Powered by: [NASA-APOD]

[NASA-APOD]: <https://apod.nasa.gov/apod/>
[Wojciech Kula]: <https://www.linkedin.com/in/wojciechkula/>
