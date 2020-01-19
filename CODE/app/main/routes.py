from flask import render_template, request, Blueprint, jsonify
from app.api_call_pred import api_call
import datetime
import json
import traceback

main = Blueprint('main', __name__)

# home page
@main.route("/")
@main.route("/home")
def home():
    return render_template('index.html')

@main.route("/explore")
def explore():
    return render_template('explore.html')

@main.route("/prediction")
def predictionmap():
    return render_template('prediction.html')



#API to get user inputs
@main.route('/prediction', methods=['POST'])
def prediction():
    try:
        req_data = request.get_json()
        origin = req_data['origin']
        destination = req_data['destination']
        date_time = req_data['datetime']

        #process time
        tm = datetime.datetime.strptime(date_time,'%Y/%m/%d %H:%M').strftime('%Y-%m-%dT%H:%M')

        out = api_call(origin, destination, tm)

        return json.dumps(out)

    except:

        return jsonify({'trace': traceback.format_exc()})

