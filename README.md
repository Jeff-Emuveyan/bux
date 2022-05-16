# Bux - technical assignment solution

This application helps users track the prices of products in real time. It displays an inteface that shows the user the current price of a product, the previous closing 
price of the product and the percentage increase or decrease of the product.

The **first screen** shows a list of predefined products. It also allows the user type in any product identifier and search for it by clicking on the search button.

The **second screen** shows the real time price values for the product. If the product has increased in value, a green arrow is shown. If not, a red arrow is shown.

<p float="left">
  <img src="https://firebasestorage.googleapis.com/v0/b/memo-24031.appspot.com/o/Screenshot_20220515_173451.png?alt=media&token=ff71bb00-c9b1-4e59-bc3f-8f6c3f462044" width="300" height="650" />
  &nbsp;
  &nbsp;
  <img src="https://firebasestorage.googleapis.com/v0/b/memo-24031.appspot.com/o/Screenshot_20220515_173759.png?alt=media&token=e59d1363-4e58-4f3a-aacb-0d61376bac78" width="300" height="650" /> 
</p>

## Structure
This project has three modules:
1) ```app```: This is the container module of the application. its sole duty is to display all features of the application
2) ```core```: The core module serves as the base module. This is the module all other modules depend on and get thier dependencies from. 
3) ```product```: This module serves like a feature module. Its purpose is to create a user interface that can allow users do two things: search for products and
view the real time updates of these products.

## Dependencies
- Retrofit
- Hilt
- Mockito
- Coroutines
- Nav graph
- Flows

## Architecture
MVVM

## Unit tests
Unit tests can be found in the ```product``` and ```core``` module.
