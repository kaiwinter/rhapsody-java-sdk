# rhapsody-java-sdk
Pure Java wrapper for the [Rhapsody/Napster REST API](https://developer.napster.com/api/v1). It handles:
* user authentication
* storage of the access and refresh token
* re-authentication by using the refresh token

The following REST methods are currently available:
* /oauth/token
* /oauth/access_token
* /v1/genres
* /v1/genres/{genreId}/albums/new
* /v1/albums/{albumId}
* /v1/albums/new
* /v1/artists/{artistId}
* /v1/artists/{artistId}/bio
* /v1/artists/{artistId}/albums/new
* /v1/me/account
* /v1/me/library/artists
* /v1/me/library/artists/{artistId}/albums
* /v1/me/library/albums
* /v1/me/charts/tracks
* /v1/me/charts/artists
* /v1/me/charts/albums
* /v1/me/library/albums (DELETE)
* /v1/me/library/albums/{albumId} (POST)

The REST service is triggered with the help of [retrofit](https://github.com/square/retrofit).
Thanks to retrofit adding more REST methods to the wrapper can be done with very little effort.

## Usage
	public class Demo {
		private static final String API_KEY = "get it from developer console";
		private static final String API_SECRET = "get it from developer console";

		private static RhapsodySdkWrapper rhapsodySdkWrapper;

		public static void main(String[] args) {
			rhapsodySdkWrapper = new RhapsodySdkWrapper(API_KEY, API_SECRET, new PreferencesAuthorizationStore());
			String username = "ask user";
			String password = "ask user";
			rhapsodySdkWrapper.authorize(username, password, new AuthenticationCallback() {
				@Override
				public void success() {
					loadGenres();
				}
			});
		}

		private static void loadGenres() {
			rhapsodySdkWrapper.loadGenres(new Callback<Collection<GenreData>>() {

				@Override
				public void success(Collection<GenreData> t, Response response) {
					String message = String.format("Loaded %d genres", t.size());
					System.out.println(message);
				}

				@Override
				public void failure(RetrofitError error) {
					System.out.println(error.getMessage());
				}
			});
		}
	}
	
### Output
	10:54:24.789 [INFO ] RhapsodySdkWrapper - Authorizing
	10:54:25.850 [INFO ] RhapsodySdkWrapper - Successfully authorized, access token: ZTU4Y2JmMDUtOTAxNi11ZDVkLThiZDAtNDUzZGJkYzU5M2U1
	10:54:25.853 [INFO ] RhapsodySdkWrapper - Loading genres
	Loaded 21 genres

## Maven
rhapsody-java-sdk is not on Maven Central. Find it on [JitPack.io](https://jitpack.io/#kaiwinter/rhapsody-java-sdk/1.3.0)
```xml
<repository>
	 <id>jitpack.io</id>
	 <url>https://jitpack.io</url>
</repository>

<dependency>
	 <groupId>com.github.kaiwinter</groupId>
	 <artifactId>rhapsody-java-sdk</artifactId>
	 <version>1.4.0</version>
</dependency>
```

## License
     Copyright 2015 Kai Winter
     
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
     
         http://www.apache.org/licenses/LICENSE-2.0
     
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
