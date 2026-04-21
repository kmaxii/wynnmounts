const fs = require("fs");

exports.preCommit = (props) => {
    const replace = (path, searchValue, replaceValue) => {
        let content = fs.readFileSync(path, "utf-8");
        if (content.match(searchValue)) {
            fs.writeFileSync(path, content.replace(searchValue, replaceValue));
            console.log(`"${path}" changed`);
        }
    };

    // Replace mod_version=1.0.0 with the new version in gradle.properties
    replace("./gradle.properties", /(?<=mod_version=)\d+\.\d+\.\d+/g, props.version);
};
