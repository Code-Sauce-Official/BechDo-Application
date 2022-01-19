![bd](https://user-images.githubusercontent.com/57208352/149821063-97bfb91c-3d83-4804-9a85-7b1c76ea1167.png)
# BechDo
Do you think you made a mistake by buying some goods in your freshman year that you no longer need? Do you feel the urge to clear up that clutter and make room for something more useful? Are you up for selling/renting it to your juniors so that they don't end up making the same mistake and could save a few bucks?

Well then we've got your back! Presenting **BechDo**,

✨List your product on the app so that potential buyers from your college, i.e. your juniors can contact you via the inbuilt chat functionality and seal the deal. You can even keep a track of your active products and mark them as unavailable or permanently delete them if they have been sold.

✨The ones looking to buy some stuff can simply go through the various products offered, filter them based on type(For sale or Rent), college, category and/or price, add some of them to their wishlist and obviously contact the seller.

So what are you guys waiting for! **BechDo pe BechDo** ya _Khareed Lo_ 🙃

`Join Code Sauce discord server for project updates, weekly meetups and insights.`

[![Discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/32HyKKzjSr) [![Youtube](https://img.shields.io/badge/Youtube-FF4747?style=for-the-badge&logo=youtube&logoColor=white)](https://www.youtube.com/channel/UCCPwX0xdVigPTksRMnpxr6g)

## Tech Stack

[![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)](https://www.figma.com/) [![Adobe XD](https://img.shields.io/badge/Adobe%20XD-FF61F6?style=for-the-badge&logo=Adobe%20XD&logoColor=white)](https://www.adobe.com/products/xd.html?sdid=12B9F15S&mv=Search&ef_id=CjwKCAjwwqaGBhBKEiwAMk-FtLdh_6WSOFgrFUSXMn7OV-3yYdf47-XcDa2PbqiybAFRYmLx7eRYsRoCkDcQAvD_BwE:G:s&s_kwcid=AL!3085!3!526748867264!b!!g!!experience%20design%20adobe!1641846445!65452677271) [![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/) [![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/) 

## Features✨

- `Authentication` : This is implemented using Firebase Auth. A first-time user needs to sign up upon opening the app. On doing the same, the user will receive a verification mail. The link in the mail will verify the user after which they can sign in. If the mail is not sent due to any error, there's an option for the user to request for it again at an interval of 30 seconds. The user upon forgetting the password can use an option to reset their password, through which they will receive a mail, which will direct them to change the same. 

- `Create and Edit Profile` : Users need to fill out a profile section mentioning their name, date of birth, college name and year, profile picture is optional. The college name has to be selected from a drop-down list. In case, user's college is not present in the list then they will have to <a href="mailto:bechdoofficial@gmail.com" >contact us<a> to get it updated on our server. College name and date of birth cannot be changed once entered. 
  
- `Sell/Rent Products` : While listing a product, users need to provide a short description, title, category and images (atmost 7). Images are compressed while uploading to 25 % of their quality, still smaller image sizes are preferable.
  
- `Track/Remove products` : The sellers can track the products posted by them in the Active Products section. They can also mark them as unavailable temporarily (after which they will be visible in the Sold Products section) or remove them permanently.
  
- `Search Products/Add Filters` : Users can make use of title based search for Products and can also filter them based on type(for sale/rent/both), category, college and price.
	
- `Favourites` : Users can mark the products as favourites but this is limited to 10 products (due to Firestore's restriction on whereIn query on arrays, which has been employed to fetch products using an array of favouriteProductIds of user, stored online to provide a multi-device experience).
  
- `Messages` : Buyers and sellers can chat with each other to negotiate price and discuss the sale. The Chat functionality has been implemented using Firebase Realtime Database. Users also get push notifications for any new messages (not real-time but updated every 15 minutes using Jetpack Work Manager).
  
- `Help` :  Users can check out this FAQs section in the app to clear out their queries.  

## Maintainers

<table>
<tr>
  
<td align="center"><a href="https://github.com/SHITIZ-AGGARWAL"><img src="https://user-images.githubusercontent.com/53532851/120942660-14795200-c748-11eb-8e5d-1f0924f1ed67.jpg?s=100" width="120px;" alt=""  style="border-radius: 50px" /><br /><sub><b>Shitiz Aggarwal</b></sub></a><br /><p>Product Designer/Web</p></td>

<td align="center"><a href="https://github.com/Acash512"><img src="https://user-images.githubusercontent.com/53511962/120997371-c2b2e500-c7a4-11eb-9269-8ea93264414a.jpg" width="120px;" alt=""  style="border-radius: 50px" /><br /><sub><b>Aakash Gupta</b></sub></a><br /><p>Lead Android Developer</p></td>

 <td align="center"><a href="https://github.com/nidhisingh-1"><img src="https://avatars.githubusercontent.com/u/61702147?v=4" width="120px;" alt=""  style="border-radius: 50px" /><br /><sub><b>Nidhi Singh</b></sub></a><br /><p>Android/Web Developer</p></td>

<td align="center"><a href="https://github.com/yatharthmago01"><img src="https://user-images.githubusercontent.com/53532851/120995152-c2b1e580-c7a2-11eb-8c34-910f87bc2cf1.png" width="120px;" alt=""  style="border-radius: 50px" /><br /><sub><b>Yatharth Mago</b></sub></a><br /><p>Android/Web Developer</p></td>
  
<td align="center"><a href="https://github.com/titiksha01"><img src="https://avatars.githubusercontent.com/u/57208352?s=400&u=fa23251169a498c0a5fbcff5b76a149597abc9ab&v=4" width="120px;" alt=""  style="border-radius: 50px" /><br /><sub><b>Titiksha Sharma</b></sub></a><br /><p>Flutter/Web Developer</p></td>

</tr>
</table>

## Setting up the project and Installation

BechDo requires [Android studio](https://developer.android.com/studio) to run.

Install the dependencies to get started.

fork the project
```sh
Using the fork option just below your profile image
```
clone the project in your own device
```sh 
git clone 'https link'
```
make a new branch
```sh
git checkout -b 'name_of_your_new_branch' 
```
open project in Android studio and do the needful changes

## Branches

The repository has the following permanent branches:

 * **main** This contains the code which has been released.

 * **develop** This contains the latest code. All the contributing PRs must be sent to this branch. When we want to release the next version of the app, this branch is merged into the `main` branch.


## Contributing
Please read our [Contributing guidelines](https://github.com/Code-Sauce-Official/BechDo-Application/blob/main/docs/CONTRIBUTING.md) and [Code of Conduct](https://github.com/Code-Sauce-Official/BechDo-Application/blob/main/docs/CodeOfConduct.md)

If you are new to open source check out [How to Start](https://github.com/Code-Sauce-Official/BechDo-Application/blob/main/docs/HowToStart.md)

Thanks to these wonderful people ✨✨:

<table>
	<tr>
		<td>
			<a href="https://github.com/Code-Sauce-Official/BechDo-Application/graphs/contributors">
  				<img src="https://contrib.rocks/image?repo=Code-Sauce-Official/BechDo-Application" />
			</a>
		</td>
	</tr>
</table>

