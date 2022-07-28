<div id="top"></div>

<div align="center">

[![Contributors][contributors-shield]][contributors-url]
</div>



<table border="1" align="center"><tr><td><h1>REST API Framework</h1></td></tr></table>


## About The Project

* This project is a RestAssured based API testing framework.
* Underlying, it uses `RestAssured` - API testing library, `TestNG` - Third-party free library for Running tests,
  and `Reporting Extent Reports` (library for interactive and detailed reports for tests).
* This Project is one stop solution for automating all your Rest APIs.
* It is built in such a way that the core structure of this framework can be used to automate any of your REST APIs by adding your own code on top of it.
* This framework provides you the capability to log Curls for all your API requests which can help you debug incase of any issues.
* It also provides listeners and reports for better understanding of your test results.

## Contents

* [Getting Started](#started)
* [Prerequisites](#pre)
* [Installation](#install)
* [Framework Details](#FrameworkDetails)
    * [Built With](#specs)
    * [Base Api](#baseapi)
    * [Framework - What and Why?](#ww)
* [Packages](#package)
    * [Main Package](#mainpackage)
    * [Test Package](#test)
    * [Reports](#reports)
* [Contributing](#Contributing)
* [Usage](#example)
* [Acknowledgments](#acknowledgments)


<!-- GETTING STARTED -->
## Getting Started<a name="started"></a>

Below are the things required to get started with the project.



## Prerequisites<a name="pre"></a>

This Project Requires Java and gradle to be installed on your system.
  ```sh
  npm install java
  npm i gradle --save-dev
  ```


## Installation<a name="install"></a>

* Clone the repo on your local system.<br />
  ```sh
  git clone https://github.com/NAKULT/Rest-ApiFramework.git
  ```


* Resolve all gradle dependencies.
* Use gradle test command to run your test files.


## Framework Details<a name="FrameworkDetails"></a>
### Built With<a name="specs"></a>
- Java
- Gradle
- TestNG
- RestAssured (Java Library)


### Base API<a name="baseapi"></a>

We have used RestAssured RequestSpecBuilder as a basis for our API test framework. We have created a BaseApi class in which we have written wrapper methods over RestAssured.



### Framework - What and Why?<a name="ww"></a>
This framework is built using RestAssured library, and it is made in such a way that it can be reused by adding the code on top of it to automate any API. It has been made generic so that it can be used to automate any REST API.



## Packages<a name="package"></a>

### Main Package<a name="mainpackage"></a>

`src/main/java/` is the core package of framework. It contains different sub packages for various API testing functionalities. All the sub Packages will be discussed in the following sections.
* apirequestbuilder -
  This package contains classes for creating API request.

* basepackage -
  It has 2 classes BaseApi and BaseTest. BaseApi class contains all the wrapper methods over RestAssured and BaseTest class contains the pre-requisite and post requisites for our test classes.

* constants -
  Inside this package we have classes which contains enum and constants.

* listeners -
  This package contains testNG listeners and it's implementations.

* reports -
  This contains implementation of testNG extent report.

* requestpojo -
  This package holds multiple pojo class which is used to create API request.

* utils -
  Inside this package we have multiple classes which contains utils functions related to test classes.


### Test Package<a name="test"></a>

`src/test/java/` is the main test package which contains all the TestClasses, TestData and TestSchema
* It contains all the test classes which uses testNG as test framework.
* TestData contains all the data in json format required while writing testcases for different functionalities.
* TestSchema contains all the schema files used for asserting response schema.


### Reports<a name="reports"></a>

`src/main/java/reports` is the report package which uses TestNG extent reports generating an interactive and detailed HTML reports for our API test cases.



<!-- CONTRIBUTING -->
## Contributing<a name="Contributing"></a>

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request.


## Usage<a name="example"></a>

```
@Test
public static void login() {
String username = System.getProperty("username");
String password = System.getProperty("password");

LoginBuilder login = new LoginBuilder();
Login loginJson = login.createLoginBody(username, password);
BaseApi api = new BaseApi();
api.setRequestParams(
       loginJson,
       BaseApi.MethodType.POST,
       LocalConfigs.baseURI,
       BasePath.LOGIN_PATH
);
Response response = api.execute();
TestUtilFunctions.validateStatusCode(response, 200);

if (response != null) {
        cookies = response.getCookies();
        accountId = (int) TestUtilFunctions.getJsonValue(response, "accountId");
        userId = (int) TestUtilFunctions.getJsonValue(response,"id");
        userName = (String) TestUtilFunctions.getJsonValue(response,"name");
   }
}
```
```

public Login createLoginBody(String username ,String password) {
Login loginRequest = (Login) TestUtilFunctions.mapJson(
        "login.json",
        new Login());

loginRequest.setUsername(username);
loginRequest.setPassword(password);
return loginRequest;
}
```


## Acknowledgments<a name="acknowledgments"></a>

* [RestAssured Official Docs](https://rest-assured.io/)
* [TestNG Docs](https://testng.org/doc/)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/NAKULT/Rest-ApiFramework.svg?style=for-the-badge
[contributors-url]: https://github.com/NAKULT/Rest-ApiFramework/contributors
[forks-shield]: https://img.shields.io/github/forks/NAKULT/Rest-ApiFramework.svg?style=for-the-badge
[forks-url]: https://github.com/NAKULT/Rest-ApiFramework/network/members
[stars-shield]: https://img.shields.io/github/stars/NAKULT/Rest-ApiFramework.svg?style=for-the-badge
[stars-url]: https://github.com/NAKULT/Rest-ApiFramework/stargazers
[issues-shield]: https://img.shields.io/github/issues/NAKULT/Rest-ApiFramework.svg?style=for-the-badge
[issues-url]: https://github.com/NAKULT/Rest-ApiFramework/issues
<p align="right">(<a href="#top">back to top)</a></p>
