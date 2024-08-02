/**
 * This example is following frontend and backend separation.
 * 
 * Before this .js is loaded, the html skeleton is created.
 * 
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to the correct html elements.
 */

/**
 * Handles the data returned by the API, read the jsonObject and populate data into the html elements.
 * @param resultData jsonObject
 */

function handleMovieListResult(resultData) {

    console.log("handleMovieListResult: populating movie list table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + (i+1) + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, the following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieListResult
jQuery.ajax({
    datatype: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist", // Setting request url, which is mapped by MovieListServlet in MovieListServlet.java
    success: (resultData) => handleMovieListResult(resultData)
});