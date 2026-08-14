const developerSelect =
    document.getElementById("developerSelect");

const exploreBtn =
    document.getElementById("exploreBtn");

const graphNodes =
    document.getElementById("graphNodes");

const connections =
    document.getElementById("connections");

const technologyDetails =
    document.getElementById("technologyDetails");

const technologyCount =
    document.getElementById("technologyCount");

const currentDeveloper =
    document.getElementById("currentDeveloper");


let currentData = [];


// ===============================
// LOAD DEVELOPERS
// ===============================

async function loadDevelopers() {

    try {

        const response =
            await fetch("/api/developers");

        const developers =
            await response.json();

        developerSelect.innerHTML =
            `<option value="">Select Developer</option>`;

        developers.forEach(developer => {

            const option =
                document.createElement("option");

            option.value = developer;
            option.textContent = developer;

            developerSelect.appendChild(option);
        });


        // Automatically select Pawan if available

        if (developers.includes("Pawan")) {

            developerSelect.value = "Pawan";

            loadDeveloper("Pawan");
        }

    } catch (error) {

        console.error(
            "Unable to load developers:",
            error
        );
    }
}


// ===============================
// EXPLORE BUTTON
// ===============================

exploreBtn.addEventListener(
    "click",
    () => {

        const developer =
            developerSelect.value;

        if (!developer) {

            alert("Please select a developer.");

            return;
        }

        loadDeveloper(developer);
    }
);


// ===============================
// LOAD DEVELOPER GRAPH
// ===============================

async function loadDeveloper(name) {

    try {

        const response =
            await fetch(
                `/api/developers/${encodeURIComponent(name)}/technology-details`
            );

        currentData =
            await response.json();

        currentDeveloper.textContent =
            name;

        technologyCount.textContent =
            `${currentData.length} technologies discovered`;

        drawGraph(name, currentData);

    } catch (error) {

        console.error(
            "Error loading technology data:",
            error
        );
    }
}


// ===============================
// DRAW GRAPH
// ===============================

function drawGraph(
    developerName,
    data
) {

    graphNodes.innerHTML = "";

    connections.innerHTML = "";

    if (!data.length) {

        return;
    }


    /*
       Graph structure:

                  Pawan
                    |
          ---------------------
          |         |         |
        Java   Spring Boot  REST API
          |         |
          |     Hibernate
          |         |
          |        SQL
    */


    const area =
        document.getElementById("graphArea");

    const width =
        area.clientWidth;

    const height =
        area.clientHeight;


    // Developer

    const developerNode =
        createNode(
            developerName,
            "developer",
            width / 2,
            105
        );


    /*
       Direct technologies are placed
       in the first technology row.
    */

    const directTechnologies =
        data.map(item =>
            item.technology
        );


    const positions = {};

    const firstRowY = 300;

    const spacing =
        Math.min(
            230,
            (width - 250) /
            Math.max(
                directTechnologies.length,
                1
            )
        );


    const startX =
        width / 2 -
        ((directTechnologies.length - 1) *
        spacing) / 2;


    directTechnologies.forEach(
        (technology, index) => {

            positions[technology] = {

                x:
                    startX +
                    index * spacing,

                y:
                    firstRowY
            };

            createNode(
                technology,
                "technology",
                positions[technology].x,
                positions[technology].y
            );
        }
    );


    /*
       Related technologies are placed
       below their parent technology.
    */

    const relatedPositions = {};

    const levels = {};

    data.forEach(item => {

        if (
            item.relatedTechnologies &&
            item.relatedTechnologies.length
        ) {

            item.relatedTechnologies.forEach(
                related => {

                    if (
                        !positions[related] &&
                        !relatedPositions[related]
                    ) {

                        relatedPositions[related] = {
                            x: 0,
                            y: 480
                        };
                    }
                }
            );
        }
    });


    /*
       Manually create a clean tree layout
       based on the current knowledge graph.
    */

    const preferredPositions = {

        "Hibernate": {
            x: width / 2,
            y: 480
        },

        "SQL": {
            x: width / 2,
            y: 650
        }
    };


    Object.keys(relatedPositions).forEach(
        technology => {

            if (preferredPositions[technology]) {

                relatedPositions[technology] =
                    preferredPositions[technology];

            }
        }
    );


    /*
       Put remaining related technologies
       under their parent.
    */

    let fallbackIndex = 0;

    Object.keys(relatedPositions).forEach(
        technology => {

            if (
                preferredPositions[technology]
            ) {
                return;
            }

            relatedPositions[technology] = {

                x:
                    width / 2 +
                    (fallbackIndex - 1) * 180,

                y:
                    480
            };

            fallbackIndex++;
        }
    );


    Object.entries(
        relatedPositions
    ).forEach(
        ([technology, position]) => {

            positions[technology] =
                position;

            createNode(
                technology,
                "technology",
                position.x,
                position.y
            );
        }
    );


    /*
       Developer -> direct technologies
    */

    directTechnologies.forEach(
        technology => {

            drawLine(
                width / 2,
                105,
                positions[technology].x,
                positions[technology].y,
                "knows"
            );
        }
    );


    /*
       Technology -> related technology
    */

    data.forEach(item => {

        const parent =
            item.technology;

        if (
            !item.relatedTechnologies
        ) {
            return;
        }

        item.relatedTechnologies.forEach(
            related => {

                if (
                    positions[parent] &&
                    positions[related]
                ) {

                    drawLine(
                        positions[parent].x,
                        positions[parent].y,
                        positions[related].x,
                        positions[related].y,
                        "related"
                    );
                }
            }
        );
    });
}


// ===============================
// CREATE NODE
// ===============================

function createNode(
    name,
    type,
    x,
    y
) {

    const node =
        document.createElement("div");

    node.className =
        `node ${type}`;

    node.textContent =
        name;

    node.style.left =
        `${x}px`;

    node.style.top =
        `${y}px`;


    if (type === "technology") {

        node.addEventListener(
            "click",
            () => {

                selectTechnology(name);

            }
        );
    }


    graphNodes.appendChild(node);

    return node;
}


// ===============================
// DRAW CONNECTION
// ===============================

function drawLine(
    x1,
    y1,
    x2,
    y2,
    type
) {

    const line =
        document.createElementNS(
            "http://www.w3.org/2000/svg",
            "line"
        );


    line.setAttribute(
        "x1",
        x1
    );

    line.setAttribute(
        "y1",
        y1
    );

    line.setAttribute(
        "x2",
        x2
    );

    line.setAttribute(
        "y2",
        y2
    );


    if (type === "knows") {

        line.setAttribute(
            "stroke",
            "#9ca8bd"
        );

    } else {

        line.setAttribute(
            "stroke",
            "#e2a525"
        );
    }


    line.setAttribute(
        "stroke-width",
        "2.5"
    );


    line.setAttribute(
        "stroke-linecap",
        "round"
    );


    connections.appendChild(line);
}


// ===============================
// TECHNOLOGY DETAILS
// ===============================

function selectTechnology(
    technology
) {

    document
        .querySelectorAll(
            ".node.technology"
        )
        .forEach(node => {

            node.classList.remove(
                "selected"
            );

            if (
                node.textContent ===
                technology
            ) {

                node.classList.add(
                    "selected"
                );
            }
        });


    const selected =
        currentData.find(
            item =>
                item.technology ===
                technology
        );


    if (!selected) {

        return;
    }


    const related =
        selected.relatedTechnologies || [];


    technologyDetails.innerHTML = `

        <h3>${technology}</h3>

        <div class="detail-heading">
            Used By
        </div>

        <div class="used-by">
            👤 ${developerSelect.value}
        </div>

        <div class="detail-heading">
            Related Technologies
        </div>

        <div class="related-list">

            ${
                related.length
                ?
                related.map(
                    item =>
                        `<span class="related-item">
                            ${item}
                         </span>`
                ).join("")
                :
                `<span class="related-item">
                    None
                 </span>`
            }

        </div>

        <div class="about-box">

            <strong>
                ℹ About ${technology}
            </strong>

            ${getTechnologyDescription(
                technology
            )}

        </div>
    `;
}


// ===============================
// TECHNOLOGY DESCRIPTION
// ===============================

function getTechnologyDescription(
    technology
) {

    const descriptions = {

        "Java":
            "Java is a widely used programming language for building backend applications, enterprise systems and APIs.",

        "Spring Boot":
            "Spring Boot is a Java-based framework used for building production-ready backend applications with minimal configuration.",

        "REST API":
            "REST APIs allow backend applications to communicate with clients using HTTP and structured data such as JSON.",

        "Hibernate":
            "Hibernate is an ORM framework that helps Java applications communicate with relational databases using objects.",

        "SQL":
            "SQL is used to store, query and manage structured data in relational databases."
    };


    return descriptions[technology]
        ||
        "Technology information is available in the developer knowledge graph.";
}



loadDevelopers();